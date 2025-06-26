package Fourier.model;

import java.awt.Point;
import Fourier.Complex;
import Fourier.FFTUtil;
import Fourier.view.FourierView2D; // パネルサイズ取得のために使用

public class FourierModel2D extends FourierModel {

    // 1. オリジナルの3チャンネル画像データ
    private double[][][] initialOriginData_Color;

    // 2. 最初に計算されたFFTスペクトル（チャンネルごと）
    private Complex[][] initialComplexData_R;
    private Complex[][] initialComplexData_G;
    private Complex[][] initialComplexData_B;

    // 3. ユーザー操作によって変更されるFFTスペクトルデータ（チャンネルごと）
    private Complex[][] userModifiedSpectrumData_R;
    private Complex[][] userModifiedSpectrumData_G;
    private Complex[][] userModifiedSpectrumData_B;

    // 4. ユーザー操作後のパワースペクトルデータ（3チャンネルを合成してグレースケールで表示）
    private double[][] recalculatedPowerSpectrumData;

    // 5. IFFTで再構成された画像データ（チャンネルごと）
    private double[][] ifftResultData_R;
    private double[][] ifftResultData_G;
    private double[][] ifftResultData_B;
    
    // マウス情報
    private Point lastCalculationPoint;
    private boolean isAltDown;

    /**
     * カラー画像データ(double[][][])を受け取るようにコンストラクタを修正
     * @param initialColorData [width][height][3]形式のカラーデータ
     */
    public FourierModel2D(double[][][] initialColorData) {
        this.initialOriginData_Color = initialColorData;
        int width = initialColorData.length;
        int height = initialColorData[0].length;
        int channels = initialColorData[0][0].length;

        if (channels != 3) {
            throw new IllegalArgumentException("Input data must have 3 color channels (R, G, B).");
        }

        // --- 1. データをR, G, Bの各チャンネルに分割 ---
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

        // --- 2. 各チャンネルで初期FFTスペクトルを計算 ---
        this.initialComplexData_R = perform2DFFTOn(dataR);
        this.initialComplexData_G = perform2DFFTOn(dataG);
        this.initialComplexData_B = perform2DFFTOn(dataB);

        // --- 3. ユーザー操作用スペクトルを0で初期化 ---
        this.userModifiedSpectrumData_R = new Complex[height][width];
        this.userModifiedSpectrumData_G = new Complex[height][width];
        this.userModifiedSpectrumData_B = new Complex[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Complex zero = new Complex(0.0, 0.0);
                this.userModifiedSpectrumData_R[i][j] = zero;
                this.userModifiedSpectrumData_G[i][j] = zero;
                this.userModifiedSpectrumData_B[i][j] = zero;
            }
        }

        // --- 4. 初期状態での再計算 ---
        recalculatePowerSpectrumFromUserModifiedData();
        performIfftAndNotify();
    }

    // --- ゲッターメソッド ---
    public double[][][] getInitialOriginColorData() {
        return initialOriginData_Color;
    }

    public double[][] getRecalculatedPowerSpectrumData() {
        return recalculatedPowerSpectrumData;
    }

    /**
     * IFFTで再構成された3チャンネルの画像データを合成して返す
     * @return [width][height][3]形式のカラーデータ
     */
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

    public Point getLastCalculationPoint() { return lastCalculationPoint; }
    public boolean getIsAltDown() { return isAltDown; }

    /**
     * マウス操作に応じて3チャンネルすべてのスペクトルを更新
     */
    @Override
    public void computeFromMousePoint(Point point, Boolean isAltDown) {
        this.lastCalculationPoint = point;
        this.isAltDown = isAltDown;

        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;
        
        int colIndex = (int) (point.getX() * cols / FourierView2D.PANEL_WIDTH);
        int rowIndex = (int) (point.getY() * rows / FourierView2D.PANEL_HEIGHT);

        if (rowIndex >= 0 && rowIndex < rows && colIndex >= 0 && colIndex < cols) {
            if (isAltDown) {
                Complex zero = new Complex(0.0, 0.0);
                userModifiedSpectrumData_R[rowIndex][colIndex] = zero;
                userModifiedSpectrumData_G[rowIndex][colIndex] = zero;
                userModifiedSpectrumData_B[rowIndex][colIndex] = zero;
            } else {
                // `new Complex(real, imag)` を使い、正しくコピーする
                userModifiedSpectrumData_R[rowIndex][colIndex] = new Complex(initialComplexData_R[rowIndex][colIndex].getReal(), initialComplexData_R[rowIndex][colIndex].getImaginary());
                userModifiedSpectrumData_G[rowIndex][colIndex] = new Complex(initialComplexData_G[rowIndex][colIndex].getReal(), initialComplexData_G[rowIndex][colIndex].getImaginary());
                userModifiedSpectrumData_B[rowIndex][colIndex] = new Complex(initialComplexData_B[rowIndex][colIndex].getReal(), initialComplexData_B[rowIndex][colIndex].getImaginary());
            }
            
            firePropertyChange("userModifiedSpectrumData", null, null);
            recalculatePowerSpectrumFromUserModifiedData();
            performIfftAndNotify();
        }
        
        firePropertyChange("calculationPoint", null, point);
        firePropertyChange("altKeyState", null, isAltDown);
    }

    /**
     * 3チャンネルのパワースペクトルを合成して1つのグレースケールデータを生成
     */
    private void recalculatePowerSpectrumFromUserModifiedData() {
        double[][] oldData = this.recalculatedPowerSpectrumData;
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
        this.recalculatedPowerSpectrumData = newData;
        firePropertyChange("recalculatedPowerSpectrumData", oldData, this.recalculatedPowerSpectrumData);
    }

    /**
     * 3チャンネルそれぞれでIFFTを実行
     */
    private void performIfftAndNotify() {
        // 各チャンネルでIFFTを実行し、結果を実数配列として保存
        this.ifftResultData_R = perform2DIFFTOn(userModifiedSpectrumData_R);
        this.ifftResultData_G = perform2DIFFTOn(userModifiedSpectrumData_G);
        this.ifftResultData_B = perform2DIFFTOn(userModifiedSpectrumData_B);

        firePropertyChange("ifftResultData", null, null);
    }

    // --- FFT/IFFT ヘルパーメソッド ---

    // 2D FFTを実行する（インプレース）
    private Complex[][] perform2DFFTOn(double[][] data) {
        Complex[][] complexData = convertDouble2DToComplex2D(data);
        perform2DFFT(complexData);
        return complexData;
    }

    // 2D IFFTを実行する（インプレース）
    private double[][] perform2DIFFTOn(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        Complex[][] ifftInput = new Complex[rows][cols];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                 // `new Complex(real, imag)` を使い、正しくコピーする
                ifftInput[i][j] = new Complex(data[i][j].getReal(), data[i][j].getImaginary());
            }
        }
        perform2DIFFT(ifftInput);
        
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = ifftInput[i][j].getReal();
            }
        }
        return result;
    }
    // 2D FFTを実行する（インプレース）
    private void perform2DFFT(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;

        // 行方向FFT
        for (int i = 0; i < rows; i++) {
            FFTUtil.fft(data[i], 0, cols);
            FFTUtil.bitReverseReorder(data[i]);
        }
        
        // 転置
        Complex[][] transposedData = transpose(data);

        // 列方向FFT（転置後の行）
        for (int i = 0; i < cols; i++) {
            FFTUtil.fft(transposedData[i], 0, rows);
            FFTUtil.bitReverseReorder(transposedData[i]);
        }

        // 再転置で元に戻す
        transposedData = transpose(transposedData);
        // 元の配列に結果をコピーし直す
        for(int i = 0; i < rows; i++) {
            System.arraycopy(transposedData[i], 0, data[i], 0, cols);
        }
    }

    /**
     * 2D IFFTを実行する（インプレース）
     * @param data 逆変換対象の複素数配列
     */
    private void perform2DIFFT(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;

        // 行方向 IFFT
        for (int i = 0; i < rows; i++) {
            FFTUtil.ifft(data[i]); // FFTUtilのifftメソッドを呼び出す
        }

        // 転置
        Complex[][] transposedData = transpose(data);

        // 列方向 IFFT（転置後の行）
        for (int i = 0; i < cols; i++) {
            FFTUtil.ifft(transposedData[i]); // FFTUtilのifftメソッドを呼び出す
        }

        // 再転置で元に戻す
        transposedData = transpose(transposedData);
        // 元の配列に結果をコピーし直す
        for(int i = 0; i < rows; i++) {
            System.arraycopy(transposedData[i], 0, data[i], 0, cols);
        }
    }

    // double[][] から Complex[][] を生成
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

    // Complex[][] を転置
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