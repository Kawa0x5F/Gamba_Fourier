package Fourier.model;

import java.awt.Point;
import Fourier.Complex;
import Fourier.FFTUtil;
import Fourier.view.FourierView1D; // PANEL_WIDTH, PANEL_HEIGHT を使用するため

public class FourierModel1D extends FourierModel {

    // 1. オリジナルの波形データ（変更不可）
    private double[] initialOriginData; 
    
    // 2. 最初にinitialOriginDataから計算されたFFTスペクトル (シフトなし)
    private Complex[] initialComplexDataForFFT; 

    // 3. ユーザー操作によって直接変更されるFFTスペクトルデータ (シフトなし)
    private Complex[] userModifiedSpectrumData; 
    
    // 4. 最初にinitialOriginDataから計算されたパワースペクトルデータ (シフト済み)
    private double[] initialCalculatedPowerSpectrumData;

    // 5. 計算結果用のデータ（パワースペクトル） - userModifiedSpectrumDataから計算 (シフト済み)
    private double[] recalculatedPowerSpectrumData; 

    // 6. userModifiedSpectrumDataからIFFTで再構成された時間領域データ
    private double[] ifftResultData; 

    // ブラシの半径を定義するフィールド
    private int brushSize = 5;

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

    public void setBrushSize(int brushSize) {
        this.brushSize = Math.max(0, brushSize);
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
        // パワースペクトルをシフトして直流成分を中央にする
        FFTUtil.shift(powerSpectrum);
        return powerSpectrum;
    }

    // --- メインロジック ---
    @Override
    public void computeFromMousePoint(Point point, Boolean isAltDown) {
        this.lastCalculationPoint = point;
        this.isAltDown = isAltDown;
        
        if (userModifiedSpectrumData != null && userModifiedSpectrumData.length > 0 && initialComplexDataForFFT != null) {
            // X座標をブラシの中心となるインデックスにマッピング
            int centerIndex = (int) (point.getX() * userModifiedSpectrumData.length / FourierView1D.PANEL_WIDTH); 
            
            // ブラシの範囲（中心から左右に brushSize 分）をループ処理
            for (int i = centerIndex - brushSize; i <= centerIndex + brushSize; i++) {
                
                // 処理対象のインデックス `i` が配列の範囲内にあるかチェック
                if (i >= 0 && i < userModifiedSpectrumData.length) {
                    int index = i; // 変数名を合わせる

                    // 表示されているインデックス(シフト済み)を、内部データ用のインデックス(シフトなし)に変換
                    int N = userModifiedSpectrumData.length;
                    int halfN = N / 2;
                    int unshiftedIndex = (index < halfN) ? (index + halfN) : (index - halfN);

                    if (isAltDown) {
                        // Altキーが押されたら、該当する周波数成分を0にリセットする
                        userModifiedSpectrumData[unshiftedIndex] = new Complex(0, 0); 
                    } else {
                        // クリックされた位置に対応する「元のスペクトルデータ」を取得
                        Complex originalSpectrumValue = initialComplexDataForFFT[unshiftedIndex];
                        // ユーザーが操作するスペクトルデータに、元のスペクトル値をセットする
                        userModifiedSpectrumData[unshiftedIndex] = new Complex(originalSpectrumValue.getReal(), originalSpectrumValue.getImaginary());
                    }
                }
            }
            
            // ループ処理が終わった後、一度だけ更新通知を行う
            firePropertyChange("userModifiedSpectrumData", null, this.userModifiedSpectrumData); 
            recalculateSpectrumFromUserModifiedData();
            performIfftAndNotify();
        }
        
        firePropertyChange("calculationPoint", null, point);
        firePropertyChange("altKeyState", null, isAltDown);
    }

    // ユーザーが操作したスペクトルデータからパワースペクトルを再計算し、Viewに通知するメソッド
    private void recalculateSpectrumFromUserModifiedData() {
        double[] oldCalculatedData = this.recalculatedPowerSpectrumData;
        
        if (this.userModifiedSpectrumData == null || this.userModifiedSpectrumData.length == 0) {
            this.recalculatedPowerSpectrumData = new double[0];
            firePropertyChange("recalculatedPowerSpectrumData", oldCalculatedData, this.recalculatedPowerSpectrumData);
            return;
        }

        this.recalculatedPowerSpectrumData = calculatePowerSpectrumFromFFTResult(this.userModifiedSpectrumData);
        firePropertyChange("recalculatedPowerSpectrumData", oldCalculatedData, this.recalculatedPowerSpectrumData);
    }

    // IFFTを実行し、時間領域の波形を再構成してViewに通知するメソッド
    private void performIfftAndNotify() {
        if (this.userModifiedSpectrumData == null || this.userModifiedSpectrumData.length == 0) {
            System.err.println("Model1D: IFFT - userModifiedSpectrumData is null or empty.");
            return;
        }
        
        Complex[] ifftInput = new Complex[userModifiedSpectrumData.length];
        for (int i = 0; i < userModifiedSpectrumData.length; i++) {
            ifftInput[i] = new Complex(userModifiedSpectrumData[i].getReal(), userModifiedSpectrumData[i].getImaginary());
        }

        FFTUtil.ifft(ifftInput);
        
        double[] newIfftResultData = new double[ifftInput.length];
        for (int i = 0; i < ifftInput.length; i++) {
            newIfftResultData[i] = ifftInput[i].getReal();
        }
        this.ifftResultData = newIfftResultData;

        firePropertyChange("ifftResultData", null, this.ifftResultData);
    }

    /**
     * ユーザーが操作するスペクトルデータをすべてゼロ（クリア）にします。
     */
    public void clearUserSpectrum() {
        if (this.userModifiedSpectrumData == null) return;

        Complex zero = new Complex(0.0, 0.0);
        for (int i = 0; i < this.userModifiedSpectrumData.length; i++) {
            this.userModifiedSpectrumData[i] = zero;
        }

        // 変更をビューに反映させるために、関連する計算を実行し通知する
        recalculateSpectrumFromUserModifiedData();
        performIfftAndNotify();
    }

    /**
     * ユーザーが操作するスペクトルデータを、最初に計算されたスペクトルデータですべて置き換えます（フィル）。
     */
    public void fillUserSpectrum() {
        if (this.userModifiedSpectrumData == null || this.initialComplexDataForFFT == null) return;
        if (this.userModifiedSpectrumData.length != this.initialComplexDataForFFT.length) return;

        for (int i = 0; i < this.userModifiedSpectrumData.length; i++) {
            Complex originalSpectrumValue = this.initialComplexDataForFFT[i];
            // 新しいComplexインスタンスを作成して代入する
            this.userModifiedSpectrumData[i] = new Complex(originalSpectrumValue.getReal(), originalSpectrumValue.getImaginary());
        }

        // 変更をビューに反映させるために、関連する計算を実行し通知する
        recalculateSpectrumFromUserModifiedData();
        performIfftAndNotify();
    }
}