package Fourier.model;

import java.awt.Point;

import Fourier.Complex;
import Fourier.FFTUtil; // 新しく作成したFFTUtilをインポート
import Fourier.view.FourierView2D; // FourierView2D が利用されていないが、インポートは残す

public class FourierModel2D extends FourierModel {

    // 2Dモデル用のデータ
    private double[][] initialOriginData; // 元の2Dデータ
    private double[][] spectrumRecalculationInputData; // スペクトル再計算用の入力データ（全て0から始まる）
    private Complex[][] complexDataForRecalculationFFT; // spectrumRecalculationInputDataをComplex化したもの
    private double[][] calculatedPowerSpectrumData; // 計算結果のパワースペクトル

    // マウス情報
    private Point lastCalculationPoint;
    private boolean isAltDown;

    public FourierModel2D(double[][] initialData) {
        this.initialOriginData = initialData;

        // spectrumRecalculationInputData を全て0で初期化
        int rows = initialData.length;
        int cols = initialData[0].length;
        this.spectrumRecalculationInputData = new double[rows][cols];
        // デフォルトで0初期化されるので、明示的なループは不要だが意図を明確にするため
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.spectrumRecalculationInputData[i][j] = 0.0;
            }
        }

        // complexDataForRecalculationFFT も spectrumRecalculationInputData を元に初期化
        this.complexDataForRecalculationFFT = convertDouble2DToComplex2D(this.spectrumRecalculationInputData);

        // 初期計算を実行
        recalculateSpectrum2D();
    }

    // --- ゲッターメソッド ---
    public double[][] getInitialOriginData() {
        return initialOriginData;
    }

    public double[][] getSpectrumRecalculationInputData() {
        return spectrumRecalculationInputData;
    }

    // 計算結果のパワースペクトルを取得
    public double[][] getCalculatedPowerSpectrumData() {
        return calculatedPowerSpectrumData;
    }

    // フーリエ変換結果の実部を取得（2Dの場合、特定の軸の実部など）
    // 現状のViewの要求と一致させるため、全実部を返す（View側でどう表示するかはViewの責務）
    public double[][] getRealPartOfRecalculatedSpectrum() {
        if (complexDataForRecalculationFFT == null) return new double[0][0];
        int rows = complexDataForRecalculationFFT.length;
        int cols = complexDataForRecalculationFFT[0].length;
        double[][] realPart = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                realPart[i][j] = complexDataForRecalculationFFT[i][j].getReal();
            }
        }
        return realPart;
    }

    public Point getLastCalculationPoint() {
        return lastCalculationPoint;
    }

    public boolean getIsAltDown() {
        return isAltDown;
    }


    // --- ヘルパーメソッド ---
    // double[][] から Complex[][] を生成するヘルパーメソッド
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

    // Complex[][] を転置するヘルパーメソッド（既存のものを静的に変更）
    public static Complex[][] transpose(Complex[][] matrix) {
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

    // --- computeFromMousePointの実装 ---
    @Override
    public void computeFromMousePoint(Point point, Boolean isAltDown) {
        this.lastCalculationPoint = point;
        this.isAltDown = isAltDown;
        System.out.println("Model2D: computeFromMousePoint called with Point=" + point + ", Alt=" + isAltDown);

        if (spectrumRecalculationInputData != null && spectrumRecalculationInputData.length > 0 && spectrumRecalculationInputData[0].length > 0) {
            // マウス座標を2D配列のインデックスにマッピング (View2Dのパネルサイズを考慮)
            // ここではView2D.PANEL_WIDTHとPANEL_HEIGHTを仮定
            int rows = spectrumRecalculationInputData.length;
            int cols = spectrumRecalculationInputData[0].length;
            
            // 例: x座標を列に、y座標を行にマッピング
            int colIndex = (int) (point.getX() * cols / 400); // 仮にView2Dの幅が400
            int rowIndex = (int) (point.getY() * rows / 400); // 仮にView2Dの高さが400

            if (rowIndex >= 0 && rowIndex < rows && colIndex >= 0 && colIndex < cols) {
                System.out.println("Model2D: Modifying spectrumRecalculationInputData at [" + rowIndex + "][" + colIndex + "]");
                
                if (isAltDown) {
                    this.spectrumRecalculationInputData[rowIndex][colIndex] = 0.0; // Altが押されたら0にリセット
                    System.out.println("Model2D: Resetting to 0.0.");
                } else {
                    this.spectrumRecalculationInputData[rowIndex][colIndex] += 50.0; // 50を加算
                    System.out.println("Model2D: Adding 50.0. New value: " + this.spectrumRecalculationInputData[rowIndex][colIndex]);
                }
                
                // spectrumRecalculationInputDataの変更をcomplexDataForRecalculationFFTに反映
                this.complexDataForRecalculationFFT = convertDouble2DToComplex2D(this.spectrumRecalculationInputData);
                
                // スペクトルを再計算
                recalculateSpectrum2D();
                
                // Viewに通知
                firePropertyChange("spectrumRecalculationInputData", null, this.spectrumRecalculationInputData); 
                firePropertyChange("complexDataForRecalculationFFT", null, this.complexDataForRecalculationFFT);

            } else {
                System.out.println("Model2D: Calculated index [" + rowIndex + "][" + colIndex + "] is out of bounds.");
            }
        } else {
            System.out.println("Model2D: spectrumRecalculationInputData is null or empty, cannot modify.");
        }
        
        // 計算ポイントとAltキーの状態の変更もViewに通知
        firePropertyChange("calculationPoint", null, point);
        firePropertyChange("altKeyState", null, isAltDown);
    }

    // 2D FFTとパワースペクトル計算のメソッド
    public void recalculateSpectrum2D() {
        double[][] oldCalculatedData = this.calculatedPowerSpectrumData;
        
        if (this.complexDataForRecalculationFFT == null || this.complexDataForRecalculationFFT.length == 0 || this.complexDataForRecalculationFFT[0].length == 0) {
            this.calculatedPowerSpectrumData = new double[0][0];
            System.out.println("Model2D: recalculateSpectrum2D - complexDataForRecalculationFFT is null or empty, no calculation.");
            firePropertyChange("calculatedPowerSpectrumData", oldCalculatedData, this.calculatedPowerSpectrumData);
            return;
        }

        int rows = complexDataForRecalculationFFT.length;
        int cols = complexDataForRecalculationFFT[0].length;
        System.out.println("Model2D: recalculateSpectrum2D - Performing 2D FFT for [" + rows + "x" + cols + "]");

        // FFTは配列をインプレースで変更するため、コピーを作成してFFTに渡す
        Complex[][] tempComplexData = new Complex[rows][cols];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                tempComplexData[i][j] = this.complexDataForRecalculationFFT[i][j];
            }
        }
        
        // 2D FFT（行→転置→行→転置）
        // 行方向FFT
        for (int i = 0; i < rows; i++) {
            Complex[] row = tempComplexData[i];
            // 2のべき乗チェック（FFTUtil.fft内で再度行われるが、ここでも確認可能）
            if ((row.length & (row.length - 1)) != 0) {
                System.err.println("Model2D: Row " + i + " length is not a power of 2 for FFT: " + row.length);
                return; // エラー処理
            }
            FFTUtil.fft(row, 0, row.length);
            FFTUtil.bitReverseReorder(row);
        }

        // 転置
        tempComplexData = transpose(tempComplexData);

        // 列方向FFT（転置後の行）
        for (int i = 0; i < cols; i++) { // cols が転置後の行数
            Complex[] colAsRow = tempComplexData[i];
            if ((colAsRow.length & (colAsRow.length - 1)) != 0) {
                System.err.println("Model2D: Column (transposed row) " + i + " length is not a power of 2 for FFT: " + colAsRow.length);
                return; // エラー処理
            }
            FFTUtil.fft(colAsRow, 0, colAsRow.length);
            FFTUtil.bitReverseReorder(colAsRow);
        }

        // 再転置で元に戻す
        tempComplexData = transpose(tempComplexData);

        // パワースペクトルを計算
        double[][] newCalculatedData = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newCalculatedData[i][j] = tempComplexData[i][j].magnitude() * tempComplexData[i][j].magnitude();
            }
        }
        this.calculatedPowerSpectrumData = newCalculatedData;

        System.out.println("Model2D: 2D FFT calculation complete. Notifying View.");
        firePropertyChange("calculatedPowerSpectrumData", oldCalculatedData, this.calculatedPowerSpectrumData);
    }

    // 2D IFFT（必要であれば）
    public void ifft2D() {
        if (this.complexDataForRecalculationFFT == null || this.complexDataForRecalculationFFT.length == 0 || this.complexDataForRecalculationFFT[0].length == 0) {
            System.err.println("Model2D: IFFT2D - complexDataForRecalculationFFT is null or empty.");
            return;
        }

        int rows = complexDataForRecalculationFFT.length;
        int cols = complexDataForRecalculationFFT[0].length;

        // IFFTは配列をインプレースで変更するため、コピーを作成
        Complex[][] tempComplexData = new Complex[rows][cols];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                tempComplexData[i][j] = this.complexDataForRecalculationFFT[i][j];
            }
        }

        // 行方向 IFFT
        for (int i = 0; i < rows; i++) {
            FFTUtil.ifft(tempComplexData[i]);
        }

        // 転置
        tempComplexData = transpose(tempComplexData);

        // 列方向（転置後の行）IFFT
        for (int i = 0; i < cols; i++) {
            FFTUtil.ifft(tempComplexData[i]);
        }

        // 再転置して元に戻す
        tempComplexData = transpose(tempComplexData);

        // 結果をcomplexDataForRecalculationFFTに反映
        this.complexDataForRecalculationFFT = tempComplexData;
        firePropertyChange("complexDataForRecalculationFFT", null, this.complexDataForRecalculationFFT);
    }
}