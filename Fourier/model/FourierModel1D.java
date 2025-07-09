package Fourier.model;

import java.awt.Point;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Fourier.Complex;
import Fourier.FFTUtil;
import Fourier.view.FourierView1D;

public class FourierModel1D extends FourierModel {

    private double[] initialOriginData; 
    private Complex[] initialComplexDataForFFT; 
    private Complex[] userModifiedSpectrumData; 
    private double[] initialCalculatedPowerSpectrumData;
    private double[] recalculatedPowerSpectrumData; 
    private double[] ifftResultData; 
    private Point lastCalculationPoint;
    private boolean isAltDown;

    // [高速化] FFT/IFFTの回転因子を事前に計算して保持
    private Complex[] twiddles;
    private Complex[] invTwiddles;

    // 計算タスクを順番に実行するためのシングルスレッドExecutor
    private final ExecutorService calculationExecutor = Executors.newSingleThreadExecutor();
    
    // 定期的な計算実行用のタイマー
    private Timer periodicTimer;
    private boolean hasPendingCalculation = false;

    public FourierModel1D(double[] initialData) {
        this.initialOriginData = initialData;
        
        // 定期的な計算タイマーを先に初期化（エラー時でも問題ないように）
        int periodicInterval = 1000; // 1秒間隔
        periodicTimer = new Timer(periodicInterval, e -> {
            if (hasPendingCalculation) {
                submitCalculationTask();
                hasPendingCalculation = false;
            }
        });
        periodicTimer.setRepeats(true);
        
        int N = initialData.length;
        if ((N & (N - 1)) != 0) {
            System.err.println("Model1D: Initial FFT input size is not a power of 2: " + N);
            // エラー時でも最低限の初期化を行う
            this.initialCalculatedPowerSpectrumData = new double[0];
            this.recalculatedPowerSpectrumData = new double[0];
            this.ifftResultData = new double[0];
            this.userModifiedSpectrumData = new Complex[0];
            this.initialComplexDataForFFT = new Complex[0];
            this.twiddles = new Complex[0];
            this.invTwiddles = new Complex[0];
            return;
        }

        // [高速化] 回転因子テーブルを一度だけ生成する
        this.twiddles = new Complex[N / 2];
        this.invTwiddles = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++) {
            double angle = -2 * Math.PI * k / N;
            this.twiddles[k] = new Complex(Math.cos(angle), Math.sin(angle));
            this.invTwiddles[k] = new Complex(Math.cos(-angle), Math.sin(-angle)); // 逆変換用
        }

        Complex[] tempInitialComplex = convertDoubleToComplex(initialData);
        
        // [高速化] 事前計算した回転因子を渡してFFTを実行
        FFTUtil.fft(tempInitialComplex, this.twiddles);
        this.initialComplexDataForFFT = tempInitialComplex;

        this.initialCalculatedPowerSpectrumData = calculatePowerSpectrumFromFFTResult(this.initialComplexDataForFFT);
        firePropertyChange("initialCalculatedPowerSpectrumData", null, this.initialCalculatedPowerSpectrumData); 

        this.userModifiedSpectrumData = new Complex[N];
        for (int i = 0; i < N; i++) {
            // 初期値は0で初期化（ユーザーが編集していない状態）
            this.userModifiedSpectrumData[i] = new Complex(0.0, 0.0);
        }
        
        // 初期化時は逆変換結果を元データに設定（変換処理は行わない）
        this.ifftResultData = initialData.clone();
        
        // 初期化時のrecalculatedPowerSpectrumDataは0で初期化（ユーザーがまだ何も編集していない状態）
        this.recalculatedPowerSpectrumData = new double[N];
        // 配列はデフォルトで0で初期化される
        
        // 初期化完了を通知（ビューの初期表示のため）
        firePropertyChange("recalculatedPowerSpectrumData", null, this.recalculatedPowerSpectrumData);
        firePropertyChange("userModifiedSpectrumData", null, this.userModifiedSpectrumData);
        firePropertyChange("ifftResultData", null, this.ifftResultData);
        
        // タイマーを開始
        periodicTimer.start();
    }
    
    // --- ゲッターメソッド (変更なし) ---
    public double[] getInitialOriginData() { return initialOriginData; }
    public double[] getRecalculatedPowerSpectrumData() { return recalculatedPowerSpectrumData; }
    public double[] getInitialCalculatedPowerSpectrumData() { return initialCalculatedPowerSpectrumData; }
    public double[] getIfftResultData() { return ifftResultData; }
    public Complex[] getUserModifiedSpectrumData() { return userModifiedSpectrumData; }
    public Point getLastCalculationPoint() { return lastCalculationPoint; }
    public boolean getIsAltDown() { return isAltDown; }

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
            
            // --- 1. 即時実行する処理（UIスレッド上） ---
            // ユーザーのブラシ操作をuserModifiedSpectrumDataに反映する
            int centerIndex = (int) (point.getX() * userModifiedSpectrumData.length / FourierView1D.PANEL_WIDTH); 
            for (int i = centerIndex - brushSize; i <= centerIndex + brushSize; i++) {
                if (i >= 0 && i < userModifiedSpectrumData.length) {
                    int unshiftedIndex = (i < userModifiedSpectrumData.length / 2) ? (i + userModifiedSpectrumData.length / 2) : (i - userModifiedSpectrumData.length / 2);

                    if (isAltDown) {
                        userModifiedSpectrumData[unshiftedIndex] = new Complex(0, 0); 
                    } else {
                        Complex originalSpectrumValue = initialComplexDataForFFT[unshiftedIndex];
                        userModifiedSpectrumData[unshiftedIndex] = new Complex(originalSpectrumValue.getReal(), originalSpectrumValue.getImaginary());
                    }
                }
            }
            
            // 変更された入力スペクトルを即座にViewに通知し、再描画させる
            firePropertyChange("userModifiedSpectrumData", null, this.userModifiedSpectrumData);
            firePropertyChange("calculationPoint", null, point);
            firePropertyChange("altKeyState", null, isAltDown);
            
            // 計算が必要であることをフラグで記録
            hasPendingCalculation = true;
        }
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

    // IFFT実行メソッド
    private void performIfftAndNotify() {
        if (this.userModifiedSpectrumData == null || this.userModifiedSpectrumData.length == 0) {
            System.err.println("Model1D: IFFT - userModifiedSpectrumData is null or empty.");
            return;
        }
        
        // IFFTはデータを破壊するため、コピーして渡す
        Complex[] ifftInput = new Complex[userModifiedSpectrumData.length];
        for (int i = 0; i < userModifiedSpectrumData.length; i++) {
            ifftInput[i] = new Complex(userModifiedSpectrumData[i].getReal(), userModifiedSpectrumData[i].getImaginary());
        }

        // [高速化] 事前計算した逆回転因子を渡してIFFTを実行
        FFTUtil.ifft(ifftInput, this.invTwiddles);
        
        double[] newIfftResultData = new double[ifftInput.length];
        for (int i = 0; i < ifftInput.length; i++) {
            newIfftResultData[i] = ifftInput[i].getReal();
        }
        this.ifftResultData = newIfftResultData;

        firePropertyChange("ifftResultData", null, this.ifftResultData);
    }

    /**
     * [並行処理] ワーカースレッド用のパワースペクトル計算メソッド
     * @return 計算結果のパワースペクトル配列
     */
    private double[] calculatePowerSpectrumFromUserModifiedDataForWorker() {
        if (this.userModifiedSpectrumData == null || this.userModifiedSpectrumData.length == 0) {
            return new double[0];
        }
        // calculatePowerSpectrumFromFFTResultは計算のみ行うのでそのまま使える
        return calculatePowerSpectrumFromFFTResult(this.userModifiedSpectrumData);
    }

    /**
     * [並行処理] ワーカースレッド用のIFFT実行メソッド
     * @return 計算結果のIFFT波形配列
     */
    private double[] performIfftForWorker() {
        if (this.userModifiedSpectrumData == null || this.userModifiedSpectrumData.length == 0) {
            return new double[0];
        }
        
        // IFFTはデータを破壊するため、コピーして渡す
        Complex[] ifftInput = new Complex[userModifiedSpectrumData.length];
        for (int i = 0; i < userModifiedSpectrumData.length; i++) {
            ifftInput[i] = new Complex(userModifiedSpectrumData[i].getReal(), userModifiedSpectrumData[i].getImaginary());
        }

        FFTUtil.ifft(ifftInput, this.invTwiddles);
        
        double[] newIfftResultData = new double[ifftInput.length];
        for (int i = 0; i < ifftInput.length; i++) {
            newIfftResultData[i] = ifftInput[i].getReal();
        }
        return newIfftResultData;
    }


    /**
     * ユーザーが操作するスペクトルデータをすべてゼロ（クリア）にします。
     */
    public void clearUserSpectrum() {
        if (this.userModifiedSpectrumData == null) return;

        if (periodicTimer != null) {
            periodicTimer.stop();
        }
        hasPendingCalculation = false;
        
        Complex zero = new Complex(0.0, 0.0);
        for (int i = 0; i < this.userModifiedSpectrumData.length; i++) {
            this.userModifiedSpectrumData[i] = zero;
        }

        // 変更をビューに反映させるために、関連する計算を実行し通知する
        recalculateSpectrumFromUserModifiedData();
        performIfftAndNotify();
        if (periodicTimer != null) {
            periodicTimer.start();
        }
    }

    /**
     * ユーザーが操作するスペクトルデータを、最初に計算されたスペクトルデータですべて置き換えます（フィル）。
     */
    public void fillUserSpectrum() {
        if (this.userModifiedSpectrumData == null || this.initialComplexDataForFFT == null) return;
        if (this.userModifiedSpectrumData.length != this.initialComplexDataForFFT.length) return;

        if (periodicTimer != null) {
            periodicTimer.stop();
        }
        hasPendingCalculation = false;

        for (int i = 0; i < this.userModifiedSpectrumData.length; i++) {
            Complex originalSpectrumValue = this.initialComplexDataForFFT[i];
            // 新しいComplexインスタンスを作成して代入する
            this.userModifiedSpectrumData[i] = new Complex(originalSpectrumValue.getReal(), originalSpectrumValue.getImaginary());
        }

        // 変更をビューに反映させるために、関連する計算を実行し通知する
        recalculateSpectrumFromUserModifiedData();
        performIfftAndNotify();
        if (periodicTimer != null) {
            periodicTimer.start();
        }
    }
    
    private void submitCalculationTask() {
        calculationExecutor.submit(() -> {
            // このブロック内はワーカースレッドで実行される
            
            // パワースペクトルの再計算とIFFTを実行
            double[] recalculatedPower = calculatePowerSpectrumFromUserModifiedDataForWorker();
            double[] ifftResult = performIfftForWorker();

            // 計算結果をUIスレッドに送ってViewに通知する
            SwingUtilities.invokeLater(() -> {
                // このブロック内はUIスレッド(EDT)で安全に実行される
                this.recalculatedPowerSpectrumData = recalculatedPower;
                this.ifftResultData = ifftResult;
                firePropertyChange("recalculatedPowerSpectrumData", null, this.recalculatedPowerSpectrumData);
                firePropertyChange("ifftResultData", null, this.ifftResultData);
            });
        });
    }
}