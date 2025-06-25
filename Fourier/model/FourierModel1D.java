package Fourier.model;

import java.awt.Point;
import Fourier.Complex;
import Fourier.FFTUtil;
import Fourier.view.FourierView1D; // PANEL_WIDTH, PANEL_HEIGHT を使用するため

public class FourierModel1D extends FourierModel {

    // 1. オリジナルの波形データ（変更不可）
    private double[] initialOriginData; 
    
    // 2. 最初にinitialOriginDataから計算されたFFTスペクトル
    private Complex[] initialComplexDataForFFT; 

    // 3. ユーザー操作によって直接変更されるFFTスペクトルデータ
    private Complex[] userModifiedSpectrumData; 
    
    // 4. 最初にinitialOriginDataから計算されたパワースペクトルデータ
    private double[] initialCalculatedPowerSpectrumData;

    // 5. 計算結果用のデータ（パワースペクトル） - userModifiedSpectrumDataから計算
    private double[] recalculatedPowerSpectrumData; 

    // 6. userModifiedSpectrumDataからIFFTで再構成された時間領域データ
    private double[] ifftResultData; 
    
    private Point lastCalculationPoint;
    private boolean isAltDown;

    public FourierModel1D() {
        // デフォルトコンストラクタ
    }

    public FourierModel1D(double[] initialData) {
        this.initialOriginData = initialData;
        
        // initialOriginDataから一度目のFFT用Complex配列を生成
        Complex[] tempInitialComplex = convertDoubleToComplex(initialData);
        if ((tempInitialComplex.length & (tempInitialComplex.length - 1)) != 0) {
            System.err.println("Model1D: Initial FFT input size is not a power of 2: " + tempInitialComplex.length);
            // エラーハンドリング：サイズが2の冪乗でない場合の処理
            return;
        }
        FFTUtil.fft(tempInitialComplex, 0, tempInitialComplex.length); // FFTを実行
        FFTUtil.bitReverseReorder(tempInitialComplex); // ビット反転順序
        this.initialComplexDataForFFT = tempInitialComplex;

        // initialOriginDataから初期のパワースペクトルを計算して保存（これは初期のFFT結果から）
        this.initialCalculatedPowerSpectrumData = calculatePowerSpectrumFromFFTResult(this.initialComplexDataForFFT);
        firePropertyChange("initialCalculatedPowerSpectrumData", null, this.initialCalculatedPowerSpectrumData); 

        // ユーザーが操作するスペクトルデータを全て0で初期化
        this.userModifiedSpectrumData = new Complex[initialComplexDataForFFT.length]; // 配列のサイズは元のFFT結果と同じ
        for (int i = 0; i < userModifiedSpectrumData.length; i++) {
            this.userModifiedSpectrumData[i] = new Complex(0.0, 0.0); // 実部も虚部も0で初期化
        }
        
        // 初期状態でのパワースペクトルとIFFT結果を計算
        recalculateSpectrumFromUserModifiedData();
        performIfftAndNotify();
    }

    // --- ゲッターメソッド ---
    public double[] getInitialOriginData() {
        return initialOriginData;
    }

    public double[] getRecalculatedPowerSpectrumData() {
        return recalculatedPowerSpectrumData;
    }

    public double[] getInitialCalculatedPowerSpectrumData() {
        return initialCalculatedPowerSpectrumData;
    }
    
    // IFFTで再構成された時間領域データ
    public double[] getIfftResultData() {
        return ifftResultData;
    }

    public Complex[] getUserModifiedSpectrumData() {
        return userModifiedSpectrumData;
    }

    public Point getLastCalculationPoint() {
        return lastCalculationPoint;
    }

    public boolean getIsAltDown() {
        return isAltDown;
    }

    // --- ヘルパーメソッド ---
    private Complex[] convertDoubleToComplex(double[] data) {
        if (data == null) return null;
        Complex[] complexArray = new Complex[data.length];
        for (int i = 0; i < data.length; i++) {
            complexArray[i] = new Complex(data[i], 0);
        }
        return complexArray;
    }

    // FFT済みのComplex配列からパワースペクトルを計算するヘルパーメソッド
    private double[] calculatePowerSpectrumFromFFTResult(Complex[] fftResultData) {
        if (fftResultData == null || fftResultData.length == 0) return new double[0];
        double[] powerSpectrum = new double[fftResultData.length];
        for(int i = 0; i < fftResultData.length; i++) {
            powerSpectrum[i] = fftResultData[i].magnitude() * fftResultData[i].magnitude();
        }
        return powerSpectrum;
    }

    // --- メインロジック ---
    @Override
    public void computeFromMousePoint(Point point, Boolean isAltDown) {
        this.lastCalculationPoint = point;
        this.isAltDown = isAltDown;
        System.out.println("Model1D: computeFromMousePoint called with Point=" + point + ", Alt=" + isAltDown);

        // userModifiedSpectrumData と initialComplexDataForFFT の null チェックを追加
        if (userModifiedSpectrumData != null && userModifiedSpectrumData.length > 0 && initialComplexDataForFFT != null) {
            // X座標を周波数ビンのインデックスにマッピング
            int index = (int) (point.getX() * userModifiedSpectrumData.length / FourierView1D.PANEL_WIDTH); 
            
            if (index >= 0 && index < userModifiedSpectrumData.length) {
                System.out.println("Model1D: Modifying userModifiedSpectrumData at index " + index + " (from x=" + point.getX() + ")");
                
                if (isAltDown) {
                    // Altキーが押されたら、該当する周波数成分を0にリセットする
                    userModifiedSpectrumData[index] = new Complex(0, 0); 
                    System.out.println("Model1D: Resetting spectrum bin " + index + " to 0.");
                } else {
                    // マウスのY座標は無視する
                    // クリックされた位置に対応する「元のスペクトルデータ」を取得
                    Complex originalSpectrumValue = initialComplexDataForFFT[index];
                    
                    // ユーザーが操作するスペクトルデータに、元のスペクトル値をセットする
                    // 安全のため、新しいComplexオブジェクトとしてコピーする
                    userModifiedSpectrumData[index] = new Complex(originalSpectrumValue.getReal(), originalSpectrumValue.getImaginary());
                    System.out.println("Model1D: Setting spectrum bin " + index + " from initial FFT data.");
                }
                
                // スペクトルが変更されたことをViewに通知
                firePropertyChange("userModifiedSpectrumData", null, this.userModifiedSpectrumData); 

                // 変更されたスペクトルからパワースペクトルを再計算
                recalculateSpectrumFromUserModifiedData();
                
                // 変更されたスペクトルから時間領域の波形を再構成（IFFT）
                performIfftAndNotify();
                
            } else {
                System.out.println("Model1D: Calculated index " + index + " is out of bounds for data length " + userModifiedSpectrumData.length);
            }
        } else {
            System.out.println("Model1D: userModifiedSpectrumData or initialComplexDataForFFT is null or empty, cannot modify.");
        }
        
        // 計算ポイントとAltキーの状態の変更もViewに通知
        firePropertyChange("calculationPoint", null, point);
        firePropertyChange("altKeyState", null, isAltDown);
    }

    // ユーザーが操作したスペクトルデータからパワースペクトルを再計算し、Viewに通知するメソッド
    private void recalculateSpectrumFromUserModifiedData() {
        double[] oldCalculatedData = this.recalculatedPowerSpectrumData;
        
        if (this.userModifiedSpectrumData == null || this.userModifiedSpectrumData.length == 0) {
            this.recalculatedPowerSpectrumData = new double[0];
            System.out.println("Model1D: recalculateSpectrumFromUserModifiedData - userModifiedSpectrumData is null or empty, no calculation performed.");
            firePropertyChange("recalculatedPowerSpectrumData", oldCalculatedData, this.recalculatedPowerSpectrumData);
            return;
        }

        Integer N = this.userModifiedSpectrumData.length;
        System.out.println("Model1D: recalculateSpectrumFromUserModifiedData - Calculating power spectrum from user modified data for N=" + N);

        // FFT済みのデータからパワースペクトルを計算
        this.recalculatedPowerSpectrumData = calculatePowerSpectrumFromFFTResult(this.userModifiedSpectrumData);

        System.out.println("Model1D: Power spectrum calculation from user modified data complete. Notifying View.");
        firePropertyChange("recalculatedPowerSpectrumData", oldCalculatedData, this.recalculatedPowerSpectrumData);
    }

    // IFFTを実行し、時間領域の波形を再構成してViewに通知するメソッド
    private void performIfftAndNotify() {
        if (this.userModifiedSpectrumData == null || this.userModifiedSpectrumData.length == 0) {
            System.err.println("Model1D: IFFT - userModifiedSpectrumData is null or empty.");
            return;
        }
        
        // IFFTはComplex配列を直接変更するため、IFFT用のコピーを作成
        Complex[] ifftInput = new Complex[userModifiedSpectrumData.length];
        for (int i = 0; i < userModifiedSpectrumData.length; i++) {
            ifftInput[i] = new Complex(userModifiedSpectrumData[i].getReal(), userModifiedSpectrumData[i].getImaginary());
        }

        FFTUtil.ifft(ifftInput); // IFFTを実行
        
        // IFFT結果（時間領域のデータ）の実部を取得
        double[] newIfftResultData = new double[ifftInput.length];
        for (int i = 0; i < ifftInput.length; i++) {
            newIfftResultData[i] = ifftInput[i].getReal();
        }
        this.ifftResultData = newIfftResultData;

        firePropertyChange("ifftResultData", null, this.ifftResultData);
        System.out.println("Model1D: IFFT calculation complete. Notifying View for ifftResultData.");
    }
}