package Fourier.model;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import Fourier.Complex;
import Fourier.FFTUtil;
import Fourier.view.FourierView2D;

/**
 * 2次元フーリエ変換のモデルクラス。
 * カラー画像のFFT、パワースペクトル計算、逆FFTなどの機能を提供します。
 */
public class FourierModel2D extends FourierModel {

    // フィールド定義
    private double[][][] initialOriginData_Color;
    private Complex[][] initialComplexData_R, initialComplexData_G, initialComplexData_B;
    private double[][] initialPowerSpectrumData;
    private Complex[][] userModifiedSpectrumData_R, userModifiedSpectrumData_G, userModifiedSpectrumData_B;
    private double[][] recalculatedPowerSpectrumData;
    private double[][] ifftResultData_R, ifftResultData_G, ifftResultData_B;
    private Point lastCalculationPoint;
    private boolean isAltDown;
    private Complex[] twiddlesRows, invTwiddlesRows;
    private Complex[] twiddlesCols, invTwiddlesCols;
    private final ExecutorService calculationExecutor = Executors.newSingleThreadExecutor();
    
    private final Timer periodicTimer;
    private boolean hasPendingCalculation = false;

    // IFFT計算用の作業用バッファ
    private Complex[][] ifftWorkspace_R, ifftWorkspace_G, ifftWorkspace_B;
    
    // 表示サイズ情報
    private int displayWidth = 400;  // デフォルト値
    private int displayHeight = 400; // デフォルト値

    /**
     * 初期カラー画像データを指定してモデルを作成します。
     * @param initialColorData 初期のカラー画像データ（[width][height][RGB]の3次元配列）
     * @throws IllegalArgumentException カラーチャンネルが3でない場合
     */
    public FourierModel2D(double[][][] initialColorData) {
        this.initialOriginData_Color = initialColorData;
        int width = initialColorData.length;
        int height = initialColorData[0].length;
        int channels = initialColorData[0][0].length;

        if (channels != 3) {
            throw new IllegalArgumentException("Input data must have 3 color channels (R, G, B).");
        }

        // FFT用の回転因子を事前計算
        this.twiddlesCols = new Complex[width / 2];
        this.invTwiddlesCols = new Complex[width / 2];
        for (int k = 0; k < width / 2; k++) {
            double angle = -2 * Math.PI * k / width;
            this.twiddlesCols[k] = new Complex(Math.cos(angle), Math.sin(angle));
            this.invTwiddlesCols[k] = new Complex(Math.cos(-angle), Math.sin(-angle));
        }
        this.twiddlesRows = new Complex[height / 2];
        this.invTwiddlesRows = new Complex[height / 2];
        for (int k = 0; k < height / 2; k++) {
            double angle = -2 * Math.PI * k / height;
            this.twiddlesRows[k] = new Complex(Math.cos(angle), Math.sin(angle));
            this.invTwiddlesRows[k] = new Complex(Math.cos(-angle), Math.sin(-angle));
        }

        // チャンネルごとのデータ準備
        double[][] dataR = new double[height][width];
        double[][] dataG = new double[height][width];
        double[][] dataB = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                dataR[y][x] = initialColorData[x][y][0];
                dataG[y][x] = initialColorData[x][y][1];
                dataB[y][x] = initialColorData[x][y][2];
            }
        }

        // 初期計算
        this.initialComplexData_R = perform2DFFTOn(dataR);
        this.initialComplexData_G = perform2DFFTOn(dataG);
        this.initialComplexData_B = perform2DFFTOn(dataB);
        calculateInitialPowerSpectrum();

        // ユーザー操作用データの初期化
        this.userModifiedSpectrumData_R = new Complex[height][width];
        this.userModifiedSpectrumData_G = new Complex[height][width];
        this.userModifiedSpectrumData_B = new Complex[height][width];
        // [高速化] IFFT作業用バッファをここで一度だけ生成する
        this.ifftWorkspace_R = new Complex[height][width];
        this.ifftWorkspace_G = new Complex[height][width];
        this.ifftWorkspace_B = new Complex[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 初期値は0で初期化（ユーザーが編集していない状態）
                Complex zero = new Complex(0.0, 0.0);
                this.userModifiedSpectrumData_R[i][j] = zero;
                this.userModifiedSpectrumData_G[i][j] = new Complex(0,0);
                this.userModifiedSpectrumData_B[i][j] = new Complex(0,0);
                
                // 作業用バッファも0で初期化
                this.ifftWorkspace_R[i][j] = new Complex(0,0);
                this.ifftWorkspace_G[i][j] = new Complex(0,0);
                this.ifftWorkspace_B[i][j] = new Complex(0,0);
            }
        }

        recalculatePowerSpectrumFromUserModifiedData();
        performIfftAndNotify();
        
        // 定期的な計算タイマーを設定（1秒間隔）
        int periodicInterval = 1000; // 1秒間隔
        periodicTimer = new Timer(periodicInterval, e -> {
            if (hasPendingCalculation) {
                submitIfftTask();
                hasPendingCalculation = false;
            }
        });
        periodicTimer.setRepeats(true);
        periodicTimer.start();
    }
    
    // --- メインロジック ---
    
    /**
     * 表示サイズを設定する
     */
    public void setDisplaySize(int width, int height) {
        this.displayWidth = width;
        this.displayHeight = height;
    }
    
    /**
     * マウス座標を画像座標に変換する
     */
    private Point convertMouseToImageCoordinates(Point mousePoint) {
        // 縦横比を保った描画でのマウス座標変換
        double scaleX = (double) displayWidth / userModifiedSpectrumData_R[0].length;
        double scaleY = (double) displayHeight / userModifiedSpectrumData_R.length;
        double scale = Math.min(scaleX, scaleY);
        
        int drawWidth = (int) (userModifiedSpectrumData_R[0].length * scale);
        int drawHeight = (int) (userModifiedSpectrumData_R.length * scale);
        
        int offsetX = (displayWidth - drawWidth) / 2;
        int offsetY = (displayHeight - drawHeight) / 2;
        
        // オフセットを考慮したマウス座標
        int adjustedX = mousePoint.x - offsetX;
        int adjustedY = mousePoint.y - offsetY;
        
        // 画像領域外の場合は元の座標をそのまま返す
        if (adjustedX < 0 || adjustedY < 0 || adjustedX >= drawWidth || adjustedY >= drawHeight) {
            return mousePoint;
        }
        
        // スケールを戻して画像座標に変換
        int imageX = (int) (adjustedX / scale);
        int imageY = (int) (adjustedY / scale);
        
        return new Point(imageX, imageY);
    }
    
    /**
     * マウス座標を画像座標に変換する（実際のパネルサイズを使用）
     */
    private Point convertMouseToImageCoordinates(Point mousePoint, int panelWidth, int panelHeight) {
        // 画像のサイズ
        int imgWidth = userModifiedSpectrumData_R[0].length;
        int imgHeight = userModifiedSpectrumData_R.length;
        
        // 縦横比を保った描画でのスケール計算
        double scaleX = (double) panelWidth / imgWidth;
        double scaleY = (double) panelHeight / imgHeight;
        double scale = Math.min(scaleX, scaleY);
        
        int drawWidth = (int) (imgWidth * scale);
        int drawHeight = (int) (imgHeight * scale);
        
        int offsetX = (panelWidth - drawWidth) / 2;
        int offsetY = (panelHeight - drawHeight) / 2;
        
        // オフセットを考慮したマウス座標
        int adjustedX = mousePoint.x - offsetX;
        int adjustedY = mousePoint.y - offsetY;
        
        // 画像領域外の場合は無効な座標を返す
        if (adjustedX < 0 || adjustedY < 0 || adjustedX >= drawWidth || adjustedY >= drawHeight) {
            return new Point(-1, -1); // 無効な座標として-1を返す
        }
        
        // スケールを戻して画像座標に変換
        int imageX = (int) (adjustedX / scale);
        int imageY = (int) (adjustedY / scale);
        
        // 境界チェック
        imageX = Math.max(0, Math.min(imgWidth - 1, imageX));
        imageY = Math.max(0, Math.min(imgHeight - 1, imageY));
        
        return new Point(imageX, imageY);
    }
    
    @Override
    public void computeFromMousePoint(Point point, Boolean isAltDown) {
        updateUserSpectrumAndRequestRepaint(point, isAltDown);
        // 計算が必要であることをフラグで記録
        hasPendingCalculation = true;
    }
    
    @Override
    public void computeFromMousePoint(Point point, Boolean isAltDown, int panelWidth, int panelHeight) {
        updateUserSpectrumAndRequestRepaint(point, isAltDown, panelWidth, panelHeight);
        // 計算が必要であることをフラグで記録
        hasPendingCalculation = true;
    }

    private void updateUserSpectrumAndRequestRepaint(Point point, Boolean isAltDown) {
        this.lastCalculationPoint = point;
        this.isAltDown = isAltDown;

        // マウス座標を画像座標に変換
        Point imagePoint = convertMouseToImageCoordinates(point);

        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;
        int centerCol = imagePoint.x;
        int centerRow = imagePoint.y;
        double radiusSquared = brushSize * brushSize;

        for (int r = centerRow - brushSize; r <= centerRow + brushSize; r++) {
            for (int c = centerCol - brushSize; c <= centerCol + brushSize; c++) {
                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                    double distanceSquared = Math.pow(c - centerCol, 2) + Math.pow(r - centerRow, 2);
                    if (distanceSquared <= radiusSquared) {
                        int unshiftedRow = (r < rows / 2) ? (r + rows / 2) : (r - rows / 2);
                        int unshiftedCol = (c < cols / 2) ? (c + cols / 2) : (c - cols / 2);
                        if (isAltDown) {
                            // [高速化] new Complex()の代わりにset()で値をリセット
                            userModifiedSpectrumData_R[unshiftedRow][unshiftedCol].set(0, 0);
                            userModifiedSpectrumData_G[unshiftedRow][unshiftedCol].set(0, 0);
                            userModifiedSpectrumData_B[unshiftedRow][unshiftedCol].set(0, 0);
                        } else {
                            // [高速化] new Complex()の代わりにset()で値をコピー
                            userModifiedSpectrumData_R[unshiftedRow][unshiftedCol].set(initialComplexData_R[unshiftedRow][unshiftedCol]);
                            userModifiedSpectrumData_G[unshiftedRow][unshiftedCol].set(initialComplexData_G[unshiftedRow][unshiftedCol]);
                            userModifiedSpectrumData_B[unshiftedRow][unshiftedCol].set(initialComplexData_B[unshiftedRow][unshiftedCol]);
                        }
                    }
                }
            }
        }
        
        firePropertyChange("userModifiedSpectrumData", null, null);
        firePropertyChange("calculationPoint", null, point);
        firePropertyChange("altKeyState", null, isAltDown);
    }
    
    private void updateUserSpectrumAndRequestRepaint(Point point, Boolean isAltDown, int panelWidth, int panelHeight) {
        this.lastCalculationPoint = point;
        this.isAltDown = isAltDown;

        // 実際のパネルサイズを使用して座標変換
        Point imagePoint = convertMouseToImageCoordinates(point, panelWidth, panelHeight);

        // 無効な座標の場合は処理をスキップ
        if (imagePoint.x < 0 || imagePoint.y < 0) {
            return;
        }

        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;
        int centerCol = imagePoint.x;
        int centerRow = imagePoint.y;
        double radiusSquared = brushSize * brushSize;

        for (int r = centerRow - brushSize; r <= centerRow + brushSize; r++) {
            for (int c = centerCol - brushSize; c <= centerCol + brushSize; c++) {
                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                    double distanceSquared = Math.pow(c - centerCol, 2) + Math.pow(r - centerRow, 2);
                    if (distanceSquared <= radiusSquared) {
                        int unshiftedRow = (r < rows / 2) ? (r + rows / 2) : (r - rows / 2);
                        int unshiftedCol = (c < cols / 2) ? (c + cols / 2) : (c - cols / 2);
                        if (isAltDown) {
                            // [高速化] new Complex()の代わりにset()で値をリセット
                            userModifiedSpectrumData_R[unshiftedRow][unshiftedCol].set(0, 0);
                            userModifiedSpectrumData_G[unshiftedRow][unshiftedCol].set(0, 0);
                            userModifiedSpectrumData_B[unshiftedRow][unshiftedCol].set(0, 0);
                        } else {
                            // [高速化] new Complex()の代わりにset()で値をコピー
                            userModifiedSpectrumData_R[unshiftedRow][unshiftedCol].set(initialComplexData_R[unshiftedRow][unshiftedCol]);
                            userModifiedSpectrumData_G[unshiftedRow][unshiftedCol].set(initialComplexData_G[unshiftedRow][unshiftedCol]);
                            userModifiedSpectrumData_B[unshiftedRow][unshiftedCol].set(initialComplexData_B[unshiftedRow][unshiftedCol]);
                        }
                    }
                }
            }
        }
        
        firePropertyChange("userModifiedSpectrumData", null, null);
        firePropertyChange("calculationPoint", null, point);
        firePropertyChange("altKeyState", null, isAltDown);
    }
    
    private void submitIfftTask() {
        calculationExecutor.submit(() -> {
            double[][][] ifftResult = performIfftForWorker();

            SwingUtilities.invokeLater(() -> {
                int height = ifftResult[0].length;
                int width = ifftResult.length;
                this.ifftResultData_R = new double[height][width];
                this.ifftResultData_G = new double[height][width];
                this.ifftResultData_B = new double[height][width];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        this.ifftResultData_R[y][x] = ifftResult[x][y][0];
                        this.ifftResultData_G[y][x] = ifftResult[x][y][1];
                        this.ifftResultData_B[y][x] = ifftResult[x][y][2];
                    }
                }
                firePropertyChange("ifftResultData", null, null);
            });
        });
    }

    public double[][] generateCurrentPowerSpectrum() {
        return recalculatePowerSpectrumFromUserModifiedDataForWorker();
    }
    
    // --- ゲッターメソッド群 (変更なし) ---
    public double[][][] getInitialOriginColorData() { return initialOriginData_Color; }
    public double[][] getInitialPowerSpectrumData() { return initialPowerSpectrumData; }
    public double[][] getRecalculatedPowerSpectrumData() { return recalculatedPowerSpectrumData; }
    public Point getLastCalculationPoint() { return lastCalculationPoint; }
    public boolean getIsAltDown() { return isAltDown; }
    public double[][][] getIfftResultColorData() {
        if (ifftResultData_R == null) return null;
        int height = ifftResultData_R.length;
        int width = ifftResultData_R[0].length;
        double[][][] resultColorData = new double[width][height][3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                resultColorData[x][y][0] = ifftResultData_R[y][x];
                resultColorData[x][y][1] = ifftResultData_G[y][x];
                resultColorData[x][y][2] = ifftResultData_B[y][x];
            }
        }
        return resultColorData;
    }
    
    // --- ヘルパーメソッド群 (変更なし) ---
    private double[][] recalculatePowerSpectrumFromUserModifiedDataForWorker() {
        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;
        double[][] newData = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double powerR = userModifiedSpectrumData_R[i][j].magnitude() * userModifiedSpectrumData_R[i][j].magnitude();
                double powerG = userModifiedSpectrumData_G[i][j].magnitude() * userModifiedSpectrumData_G[i][j].magnitude();
                double powerB = userModifiedSpectrumData_B[i][j].magnitude() * userModifiedSpectrumData_B[i][j].magnitude();
                newData[i][j] = powerR + powerG + powerB;
            }
        }
        FFTUtil.shift(newData);
        return newData;
    }

    private double[][][] performIfftForWorker() {
        // [高速化] チャンネルごとに、対応する作業用バッファを渡してIFFTを実行
        double[][] r = perform2DIFFTOn(userModifiedSpectrumData_R, ifftWorkspace_R);
        double[][] g = perform2DIFFTOn(userModifiedSpectrumData_G, ifftWorkspace_G);
        double[][] b = perform2DIFFTOn(userModifiedSpectrumData_B, ifftWorkspace_B);
        
        int height = r.length;
        int width = r[0].length;
        double[][][] resultColorData = new double[width][height][3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                resultColorData[x][y][0] = r[y][x];
                resultColorData[x][y][1] = g[y][x];
                resultColorData[x][y][2] = b[y][x];
            }
        }
        return resultColorData;
    }

    private void calculateInitialPowerSpectrum() {
        int rows = initialComplexData_R.length;
        int cols = initialComplexData_R[0].length;
        double[][] newData = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double powerR = initialComplexData_R[i][j].magnitude() * initialComplexData_R[i][j].magnitude();
                double powerG = initialComplexData_G[i][j].magnitude() * initialComplexData_G[i][j].magnitude();
                double powerB = initialComplexData_B[i][j].magnitude() * initialComplexData_B[i][j].magnitude();
                newData[i][j] = powerR + powerG + powerB;
            }
        }
        FFTUtil.shift(newData);
        this.initialPowerSpectrumData = newData;
    }
    
    private void recalculatePowerSpectrumFromUserModifiedData() {
        this.recalculatedPowerSpectrumData = recalculatePowerSpectrumFromUserModifiedDataForWorker();
        firePropertyChange("recalculatedPowerSpectrumData", null, this.recalculatedPowerSpectrumData);
    }
    
    private void performIfftAndNotify() {
        submitIfftTask();
    }
    
    private Complex[][] perform2DFFTOn(double[][] data) {
        Complex[][] complexData = convertDouble2DToComplex2D(data);
        perform2DFFT(complexData);
        return complexData;
    }

    // [高速化] IFFTの実行メソッドを、作業用バッファを受け取るように変更
    private double[][] perform2DIFFTOn(Complex[][] data, Complex[][] workspace) {
        int rows = data.length;
        int cols = data[0].length;

        // [高速化] new Complex[][] の代わりに、作業用バッファに値をコピーする
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                workspace[i][j].set(data[i][j]);
            }
        }

        perform2DIFFT(workspace); // IFFTは作業用バッファに対して実行

        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = workspace[i][j].getReal();
            }
        }
        return result;
    }

    private void perform2DFFT(Complex[][] data) {
        int rows = data.length;
        for (int i = 0; i < rows; i++) {
            FFTUtil.fft(data[i], this.twiddlesCols);
        }
        Complex[][] transposedData = transpose(data);
        int cols = transposedData.length;
        for (int i = 0; i < cols; i++) {
            FFTUtil.fft(transposedData[i], this.twiddlesRows);
        }
        transposedData = transpose(transposedData);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(transposedData[i], 0, data[i], 0, data[0].length);
        }
    }

    private void perform2DIFFT(Complex[][] data) {
        int rows = data.length;
        for (int i = 0; i < rows; i++) {
            FFTUtil.ifft(data[i], this.invTwiddlesCols);
        }
        Complex[][] transposedData = transpose(data);
        int cols = transposedData.length;
        for (int i = 0; i < cols; i++) {
            FFTUtil.ifft(transposedData[i], this.invTwiddlesRows);
        }
        transposedData = transpose(transposedData);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(transposedData[i], 0, data[i], 0, data[0].length);
        }
    }

    private Complex[][] convertDouble2DToComplex2D(double[][] data) {
        if (data == null) return null;
        int rows = data.length;
        int cols = data[0].length;
        Complex[][] complexArray = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                complexArray[i][j] = new Complex(data[i][j], 0);
            }
        }
        return complexArray;
    }
    
    public void clearUserSpectrum() {
        periodicTimer.stop();
        hasPendingCalculation = false;
        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // [高速化] new Complex()の代わりにset()で値をリセット
                this.userModifiedSpectrumData_R[i][j].set(0, 0);
                this.userModifiedSpectrumData_G[i][j].set(0, 0);
                this.userModifiedSpectrumData_B[i][j].set(0, 0);
            }
        }
        recalculatePowerSpectrumFromUserModifiedData();
        performIfftAndNotify();
        periodicTimer.start();
    }
    
    public void fillUserSpectrum() {
        periodicTimer.stop();
        hasPendingCalculation = false;
        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // [高速化] new Complex()の代わりにset()で値をコピー
                this.userModifiedSpectrumData_R[i][j].set(initialComplexData_R[i][j]);
                this.userModifiedSpectrumData_G[i][j].set(initialComplexData_G[i][j]);
                this.userModifiedSpectrumData_B[i][j].set(initialComplexData_B[i][j]);
            }
        }
        recalculatePowerSpectrumFromUserModifiedData();
        performIfftAndNotify();
        periodicTimer.start();
    }

    private Complex[][] transpose(Complex[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Complex[][] transposed = new Complex[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }
}