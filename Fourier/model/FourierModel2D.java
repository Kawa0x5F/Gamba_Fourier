package Fourier.model;

import java.awt.Point;
import Fourier.Complex;
import Fourier.FFTUtil;
import Fourier.view.FourierView2D; // パネルサイズ取得のために使用

public class FourierModel2D extends FourierModel {

    // 1. オリジナルの2D画像データ（変更不可）
    private double[][] initialOriginData; 
    
    // 2. 最初にinitialOriginDataから計算された2D-FFTスペクトル（正解データ）
    private Complex[][] initialComplexDataForFFT; 

    // 3. ユーザー操作によって直接変更されるFFTスペクトルデータ
    private Complex[][] userModifiedSpectrumData; 
    
    // 4. userModifiedSpectrumDataから計算されたパワースペクトルデータ
    private double[][] recalculatedPowerSpectrumData; 

    // 5. userModifiedSpectrumDataからIFFTで再構成された空間領域（画像）データ
    private double[][] ifftResultData; 
    
    // マウス情報
    private Point lastCalculationPoint;
    private boolean isAltDown;

    public FourierModel2D(double[][] initialData) {
        this.initialOriginData = initialData;
        int rows = initialData.length;
        int cols = initialData[0].length;

        // --- 1. 初期FFTスペクトルの計算 ---
        // 元データを2D-FFTして「正解」のスペクトルを計算し、initialComplexDataForFFTに保存
        Complex[][] tempInitialComplex = convertDouble2DToComplex2D(initialData);
        perform2DFFT(tempInitialComplex); // 2D-FFTを実行
        this.initialComplexDataForFFT = tempInitialComplex;

        // --- 2. ユーザー操作用スペクトルの初期化 ---
        // ユーザーが操作するスペクトルデータを全て0で初期化
        this.userModifiedSpectrumData = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.userModifiedSpectrumData[i][j] = new Complex(0.0, 0.0);
            }
        }
        
        // --- 3. 初期状態での再計算 ---
        // パワースペクトルとIFFT結果を初期状態で一度計算しておく
        recalculatePowerSpectrumFromUserModifiedData();
        performIfftAndNotify();
    }

    // --- ゲッターメソッド ---
    public double[][] getInitialOriginData() {
        return initialOriginData;
    }

    public Complex[][] getUserModifiedSpectrumData() {
        return userModifiedSpectrumData;
    }

    public double[][] getRecalculatedPowerSpectrumData() {
        return recalculatedPowerSpectrumData;
    }

    public double[][] getIfftResultData() {
        return ifftResultData;
    }

    public Point getLastCalculationPoint() {
        return lastCalculationPoint;
    }

    public boolean getIsAltDown() {
        return isAltDown;
    }

    // --- computeFromMousePointの実装（1D版の設計思想を適用） ---
    @Override
    public void computeFromMousePoint(Point point, Boolean isAltDown) {
        this.lastCalculationPoint = point;
        this.isAltDown = isAltDown;
        System.out.println("Model2D: computeFromMousePoint called with Point=" + point + ", Alt=" + isAltDown);

        if (userModifiedSpectrumData != null && initialComplexDataForFFT != null) {
            int rows = userModifiedSpectrumData.length;
            int cols = userModifiedSpectrumData[0].length;
            
            // マウス座標を2D配列のインデックスにマッピング
            int colIndex = (int) (point.getX() * cols / FourierView2D.PANEL_WIDTH);
            int rowIndex = (int) (point.getY() * rows / FourierView2D.PANEL_HEIGHT);

            if (rowIndex >= 0 && rowIndex < rows && colIndex >= 0 && colIndex < cols) {
                System.out.println("Model2D: Modifying userModifiedSpectrumData at [" + rowIndex + "][" + colIndex + "]");
                
                if (isAltDown) {
                    // Altキーが押されたら、該当する周波数成分を0にリセット
                    userModifiedSpectrumData[rowIndex][colIndex] = new Complex(0.0, 0.0);
                    System.out.println("Model2D: Resetting spectrum component to 0.");
                } else {
                    // 「正解」のスペクトルデータを取得
                    Complex originalSpectrumValue = initialComplexDataForFFT[rowIndex][colIndex];
                    // ユーザー操作用スペクトルに値をセット（コピー）
                    userModifiedSpectrumData[rowIndex][colIndex] = new Complex(originalSpectrumValue.getReal(), originalSpectrumValue.getImaginary());
                    System.out.println("Model2D: Setting spectrum component from initial FFT data.");
                }
                
                // スペクトルが変更されたことをViewに通知
                firePropertyChange("userModifiedSpectrumData", null, this.userModifiedSpectrumData); 

                // 変更を反映してパワースペクトルとIFFT結果を再計算
                recalculatePowerSpectrumFromUserModifiedData();
                performIfftAndNotify();

            } else {
                System.out.println("Model2D: Calculated index [" + rowIndex + "][" + colIndex + "] is out of bounds.");
            }
        } else {
            System.out.println("Model2D: Data arrays are not initialized.");
        }
        
        firePropertyChange("calculationPoint", null, point);
        firePropertyChange("altKeyState", null, isAltDown);
    }

    // --- 再計算メソッド群 ---

    // ユーザーが操作したスペクトルからパワースペクトルを再計算
    private void recalculatePowerSpectrumFromUserModifiedData() {
        double[][] oldData = this.recalculatedPowerSpectrumData;
        if (this.userModifiedSpectrumData == null) {
            this.recalculatedPowerSpectrumData = new double[0][0];
        } else {
            int rows = this.userModifiedSpectrumData.length;
            int cols = this.userModifiedSpectrumData[0].length;
            double[][] newData = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    newData[i][j] = this.userModifiedSpectrumData[i][j].magnitude() * this.userModifiedSpectrumData[i][j].magnitude();
                }
            }
            this.recalculatedPowerSpectrumData = newData;
        }
        firePropertyChange("recalculatedPowerSpectrumData", oldData, this.recalculatedPowerSpectrumData);
    }

    // ユーザーが操作したスペクトルから2D-IFFTを実行して空間領域データを再構成
    private void performIfftAndNotify() {
        if (this.userModifiedSpectrumData == null) {
            System.err.println("Model2D: IFFT - userModifiedSpectrumData is null.");
            this.ifftResultData = new double[0][0];
            firePropertyChange("ifftResultData", null, this.ifftResultData);
            return;
        }

        int rows = userModifiedSpectrumData.length;
        int cols = userModifiedSpectrumData[0].length;
        Complex[][] ifftInput = new Complex[rows][cols];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                ifftInput[i][j] = new Complex(userModifiedSpectrumData[i][j].getReal(), userModifiedSpectrumData[i][j].getImaginary());
            }
        }

        // 2D-IFFTを実行
        perform2DIFFT(ifftInput);

        double[][] newIfftResultData = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newIfftResultData[i][j] = ifftInput[i][j].getReal();
            }
        }
        this.ifftResultData = newIfftResultData;

        firePropertyChange("ifftResultData", null, this.ifftResultData);
        System.out.println("Model2D: 2D IFFT calculation complete. Notifying View for ifftResultData.");
    }

    // --- FFT/IFFT ヘルパーメソッド ---

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

    // 2D IFFTを実行する（インプレース）
    private void perform2DIFFT(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;

        // 行方向 IFFT
        for (int i = 0; i < rows; i++) {
            FFTUtil.ifft(data[i]);
        }

        // 転置
        Complex[][] transposedData = transpose(data);

        // 列方向（転置後の行）IFFT
        for (int i = 0; i < cols; i++) {
            FFTUtil.ifft(transposedData[i]);
        }

        // 再転置して元に戻す
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