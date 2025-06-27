package Fourier.model;

import java.awt.Point;
import Fourier.Complex;
import Fourier.FFTUtil;
import Fourier.view.FourierView2D; // パネルサイズ取得のために使用

public class FourierModel2D extends FourierModel {

    // 1. オリジナルの3チャンネル画像データ
    private double[][][] initialOriginData_Color;

    // 2. 最初に計算されたFFTスペクトル（チャンネルごと） (シフトなし)
    private Complex[][] initialComplexData_R;
    private Complex[][] initialComplexData_G;
    private Complex[][] initialComplexData_B;

    // 3. 最初に計算されたパワースペクトルデータ（3チャンネルを合成してグレースケールで表示） (シフト済み)
    private double[][] initialPowerSpectrumData;

    // 4. ユーザー操作によって変更されるFFTスペクトルデータ（チャンネルごと） (シフトなし)
    private Complex[][] userModifiedSpectrumData_R;
    private Complex[][] userModifiedSpectrumData_G;
    private Complex[][] userModifiedSpectrumData_B;

    // 5. ユーザー操作後のパワースペクトルデータ（3チャンネルを合成してグレースケールで表示） (シフト済み)
    private double[][] recalculatedPowerSpectrumData;

    // 6. IFFTで再構成された画像データ（チャンネルごと）
    private double[][] ifftResultData_R;
    private double[][] ifftResultData_G;
    private double[][] ifftResultData_B;
    
    // マウス情報
    private Point lastCalculationPoint;
    private boolean isAltDown;

    public FourierModel2D(double[][][] initialColorData) {
        this.initialOriginData_Color = initialColorData;
        int width = initialColorData.length;
        int height = initialColorData[0].length;
        int channels = initialColorData[0][0].length;

        if (channels != 3) {
            throw new IllegalArgumentException("Input data must have 3 color channels (R, G, B).");
        }

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

        this.initialComplexData_R = perform2DFFTOn(dataR);
        this.initialComplexData_G = perform2DFFTOn(dataG);
        this.initialComplexData_B = perform2DFFTOn(dataB);

        calculateInitialPowerSpectrum();

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

        recalculatePowerSpectrumFromUserModifiedData();
        performIfftAndNotify();
    }

    // --- ゲッターメソッド ---
    public double[][][] getInitialOriginColorData() {
        return initialOriginData_Color;
    }

    public double[][] getInitialPowerSpectrumData() {
        return initialPowerSpectrumData;
    }

    public double[][] getRecalculatedPowerSpectrumData() {
        return recalculatedPowerSpectrumData;
    }

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

    // --- メインロジック ---
    @Override
    public void computeFromMousePoint(Point point, Boolean isAltDown) {
        this.lastCalculationPoint = point;
        this.isAltDown = isAltDown;

        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;
        
        int colIndex = (int) (point.getX() * cols / FourierView2D.PANEL_WIDTH);
        int rowIndex = (int) (point.getY() * rows / FourierView2D.PANEL_HEIGHT);

        if (rowIndex >= 0 && rowIndex < rows && colIndex >= 0 && colIndex < cols) {
            // 表示上のインデックス(シフト済み)を、内部データ用のインデックス(シフトなし)に変換
            int halfRows = rows / 2;
            int halfCols = cols / 2;
            int unshiftedRow = (rowIndex < halfRows) ? (rowIndex + halfRows) : (rowIndex - halfRows);
            int unshiftedCol = (colIndex < halfCols) ? (colIndex + halfCols) : (colIndex - halfCols);

            if (isAltDown) {
                Complex zero = new Complex(0.0, 0.0);
                userModifiedSpectrumData_R[unshiftedRow][unshiftedCol] = zero;
                userModifiedSpectrumData_G[unshiftedRow][unshiftedCol] = zero;
                userModifiedSpectrumData_B[unshiftedRow][unshiftedCol] = zero;
            } else {
                userModifiedSpectrumData_R[unshiftedRow][unshiftedCol] = new Complex(initialComplexData_R[unshiftedRow][unshiftedCol].getReal(), initialComplexData_R[unshiftedRow][unshiftedCol].getImaginary());
                userModifiedSpectrumData_G[unshiftedRow][unshiftedCol] = new Complex(initialComplexData_G[unshiftedRow][unshiftedCol].getReal(), initialComplexData_G[unshiftedRow][unshiftedCol].getImaginary());
                userModifiedSpectrumData_B[unshiftedRow][unshiftedCol] = new Complex(initialComplexData_B[unshiftedRow][unshiftedCol].getReal(), initialComplexData_B[unshiftedRow][unshiftedCol].getImaginary());
            }
            
            firePropertyChange("userModifiedSpectrumData", null, null);
            recalculatePowerSpectrumFromUserModifiedData();
            performIfftAndNotify();
        }
        
        firePropertyChange("calculationPoint", null, point);
        firePropertyChange("altKeyState", null, isAltDown);
    }
    
    // --- ヘルパーメソッド ---
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
        // パワースペクトルをシフトして直流成分を中央にする
        FFTUtil.shift(newData);
        this.initialPowerSpectrumData = newData;
    }

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
        // パワースペクトルをシフトして直流成分を中央にする
        FFTUtil.shift(newData);
        this.recalculatedPowerSpectrumData = newData;
        firePropertyChange("recalculatedPowerSpectrumData", oldData, this.recalculatedPowerSpectrumData);
    }

    private void performIfftAndNotify() {
        this.ifftResultData_R = perform2DIFFTOn(userModifiedSpectrumData_R);
        this.ifftResultData_G = perform2DIFFTOn(userModifiedSpectrumData_G);
        this.ifftResultData_B = perform2DIFFTOn(userModifiedSpectrumData_B);
        firePropertyChange("ifftResultData", null, null);
    }

    private Complex[][] perform2DFFTOn(double[][] data) {
        Complex[][] complexData = convertDouble2DToComplex2D(data);
        perform2DFFT(complexData);
        return complexData;
    }

    private double[][] perform2DIFFTOn(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        Complex[][] ifftInput = new Complex[rows][cols];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
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

    private void perform2DFFT(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;

        for (int i = 0; i < rows; i++) {
            FFTUtil.fft(data[i], 0, cols);
            FFTUtil.bitReverseReorder(data[i]);
        }
        
        Complex[][] transposedData = transpose(data);

        for (int i = 0; i < cols; i++) {
            FFTUtil.fft(transposedData[i], 0, rows);
            FFTUtil.bitReverseReorder(transposedData[i]);
        }

        transposedData = transpose(transposedData);
        for(int i = 0; i < rows; i++) {
            System.arraycopy(transposedData[i], 0, data[i], 0, cols);
        }
    }

    private void perform2DIFFT(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;

        for (int i = 0; i < rows; i++) {
            FFTUtil.ifft(data[i]);
        }

        Complex[][] transposedData = transpose(data);

        for (int i = 0; i < cols; i++) {
            FFTUtil.ifft(transposedData[i]);
        }

        transposedData = transpose(transposedData);
        for(int i = 0; i < rows; i++) {
            System.arraycopy(transposedData[i], 0, data[i], 0, cols);
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

    /**
     * ユーザーが操作するスペクトルデータをすべてゼロ（クリア）にします。
     */
    public void clearUserSpectrum() {
        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;
        Complex zero = new Complex(0.0, 0.0);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.userModifiedSpectrumData_R[i][j] = zero;
                this.userModifiedSpectrumData_G[i][j] = zero;
                this.userModifiedSpectrumData_B[i][j] = zero;
            }
        }

        // 変更をビューに反映させるために、関連する計算を実行し通知する
        recalculatePowerSpectrumFromUserModifiedData();
        performIfftAndNotify();
        firePropertyChange("userModifiedSpectrumData", null, null); // スペクトルパネルの再描画をトリガー
    }

    /**
     * ユーザーが操作するスペクトルデータを、最初に計算されたスペクトルデータですべて置き換えます（フィル）。
     */
    public void fillUserSpectrum() {
        int rows = userModifiedSpectrumData_R.length;
        int cols = userModifiedSpectrumData_R[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // 新しいComplexインスタンスを作成して代入する
                this.userModifiedSpectrumData_R[i][j] = new Complex(initialComplexData_R[i][j].getReal(), initialComplexData_R[i][j].getImaginary());
                this.userModifiedSpectrumData_G[i][j] = new Complex(initialComplexData_G[i][j].getReal(), initialComplexData_G[i][j].getImaginary());
                this.userModifiedSpectrumData_B[i][j] = new Complex(initialComplexData_B[i][j].getReal(), initialComplexData_B[i][j].getImaginary());
            }
        }

        // 変更をビューに反映させるために、関連する計算を実行し通知する
        recalculatePowerSpectrumFromUserModifiedData();
        performIfftAndNotify();
        firePropertyChange("userModifiedSpectrumData", null, null); // スペクトルパネルの再描画をトリガー
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