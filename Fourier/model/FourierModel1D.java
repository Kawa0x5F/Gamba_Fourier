package Fourier.model;

import java.awt.Point;
import Fourier.Complex;
import Fourier.FFTUtil;
import Fourier.view.FourierView1D;

/**
 * 1次元フーリエ変換のモデルクラス。
 * 1次元信号のFFT、パワースペクトル計算、逆FFTなどの機能を提供します。
 */
public class FourierModel1D extends FourierModel {

    // ブラシサイズの定数
    private int brushSize = 2; // デフォルトのブラシサイズ

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

    private Point lastCalculationPoint;
    private boolean isAltDown;

    /**
     * デフォルトコンストラクタ。
     */
    public FourierModel1D() {
        // デフォルトコンストラクタ
    }

    /**
     * 初期データを指定してモデルを作成します。
     * @param initialData 初期の1次元信号データ
     */
    public FourierModel1D(double[] initialData) {
        this.initialOriginData = initialData;
        
        // initialOriginDataから一度目のFFT用Complex配列を生成
        Complex[] tempInitialComplex = convertDoubleToComplex(initialData);
        if ((tempInitialComplex.length & (tempInitialComplex.length - 1)) != 0) {
            System.err.println("Model1D: Initial FFT input size is not a power of 2: " + tempInitialComplex.length);
            // エラーハンドリング：サイズが2の冪乗でない場合の処理
            return;
        }
        // 回転因子を生成してFFTを実行
        Complex[] twiddles = generateTwiddles(tempInitialComplex.length);
        FFTUtil.fft(tempInitialComplex, twiddles); // FFTを実行
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

    /**
     * 初期のオリジナルデータを取得します。
     * @return 初期の波形データ
     */
    public double[] getInitialOriginData() {
        return initialOriginData;
    }

    /**
     * 再計算されたパワースペクトルデータを取得します。
     * @return 再計算されたパワースペクトルデータ
     */
    public double[] getRecalculatedPowerSpectrumData() {
        return recalculatedPowerSpectrumData;
    }

    /**
     * 初期計算されたパワースペクトルデータを取得します。
     * @return 初期計算されたパワースペクトルデータ
     */
    public double[] getInitialCalculatedPowerSpectrumData() {
        return initialCalculatedPowerSpectrumData;
    }
    
    /**
     * IFFTで再構成された時間領域データを取得します。
     * @return IFFT結果データ
     */
    public double[] getIfftResultData() {
        return ifftResultData;
    }

    /**
     * ユーザーが変更したスペクトルデータを取得します。
     * @return ユーザー変更スペクトルデータ
     */
    public Complex[] getUserModifiedSpectrumData() {
        return userModifiedSpectrumData;
    }

    /**
     * 最後の計算点を取得します。
     * @return 最後の計算点
     */
    public Point getLastCalculationPoint() {
        return lastCalculationPoint;
    }

    /**
     * Altキーが押されているかどうかを取得します。
     * @return Altキーが押されている場合はtrue
     */
    public boolean getIsAltDown() {
        return isAltDown;
    }

    /**
     * 現在のブラシサイズを取得します。
     * @return ブラシサイズ
     */
    public int getBrushSize() {
        return brushSize;
    }

    /**
     * ブラシサイズを設定します。
     * @param brushSize 新しいブラシサイズ（最小値は1）
     */
    public void setBrushSize(int brushSize) {
        this.brushSize = Math.max(1, brushSize); // 最小値を1に制限
    }

    /**
     * double配列をComplex配列に変換します。
     * @param data 変換するdouble配列
     * @return 変換されたComplex配列
     */
    private Complex[] convertDoubleToComplex(double[] data) {
        if (data == null) return null;
        Complex[] complexArray = new Complex[data.length];
        for (int i = 0; i < data.length; i++) {
            complexArray[i] = new Complex(data[i], 0);
        }
        return complexArray;
    }

    /**
     * FFT済みのComplex配列からパワースペクトルを計算するヘルパーメソッド
     * @param fftResultData FFT結果データ
     * @return パワースペクトルデータ
     */
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

    /**
     * 回転因子を生成するヘルパーメソッド
     * @param n データ長
     * @return 回転因子の配列
     */
    private Complex[] generateTwiddles(int n) {
        Complex[] twiddles = new Complex[n / 2];
        for (int k = 0; k < n / 2; k++) {
            double angle = -2.0 * Math.PI * k / n;
            twiddles[k] = new Complex(Math.cos(angle), Math.sin(angle));
        }
        return twiddles;
    }

    /**
     * 逆回転因子を生成するヘルパーメソッド
     * @param n データ長
     * @return 逆回転因子の配列
     */
    private Complex[] generateInverseTwiddles(int n) {
        Complex[] invTwiddles = new Complex[n / 2];
        for (int k = 0; k < n / 2; k++) {
            double angle = 2.0 * Math.PI * k / n; // 符号が逆
            invTwiddles[k] = new Complex(Math.cos(angle), Math.sin(angle));
        }
        return invTwiddles;
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

    /**
     * ユーザーが操作したスペクトルデータからパワースペクトルを再計算し、Viewに通知するメソッド
     */
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

    /**
     * IFFTを実行し、時間領域の波形を再構成してViewに通知するメソッド
     */
    private void performIfftAndNotify() {
        if (this.userModifiedSpectrumData == null || this.userModifiedSpectrumData.length == 0) {
            System.err.println("Model1D: IFFT - userModifiedSpectrumData is null or empty.");
            return;
        }
        
        Complex[] ifftInput = new Complex[userModifiedSpectrumData.length];
        for (int i = 0; i < userModifiedSpectrumData.length; i++) {
            ifftInput[i] = new Complex(userModifiedSpectrumData[i].getReal(), userModifiedSpectrumData[i].getImaginary());
        }

        FFTUtil.ifft(ifftInput, generateInverseTwiddles(ifftInput.length));
        
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