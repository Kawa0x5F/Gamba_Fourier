package Fourier.model;

import Fourier.Complex;
import Fourier.model.FourierModel1D;

/**
 * FourierView1DをテストするためのFourierModel1Dのモッククラス（修正版）
 * 実際の計算ロジックをバイパスし、テスト用のデータを返す。
 */
@SuppressWarnings("unused")
public class MockFourierModel1D extends FourierModel1D {

    private double[] dummyPowerSpectrum;
    private Complex[] dummyComplexResult;
    private double[] dummyInitialOriginData;
    private double[] dummyIfftResultData;
    private double[] dummyRecalculatedPowerSpectrumData;

    public MockFourierModel1D() {
        super();
        // デフォルトのダミーデータを設定
        this.dummyInitialOriginData = new double[]{1.0, 2.0, 3.0, 4.0};
        this.dummyPowerSpectrum = new double[]{16.0, 4.0, 0.0, 4.0};
        this.dummyComplexResult = new Complex[]{
            new Complex(4, 0), new Complex(2, 0), 
            new Complex(0, 0), new Complex(2, 0)
        };
        this.dummyIfftResultData = new double[]{1.0, 2.0, 3.0, 4.0};
        this.dummyRecalculatedPowerSpectrumData = new double[]{16.0, 4.0, 0.0, 4.0};
    }

    /**
     * テストで期待される結果データをあらかじめモックに設定しておくメソッド
     * @param dummyPowerSpectrum getCalculatedData()が返すパワースペクトルのダミーデータ
     * @param dummyComplexResult getComplexResult()が返す複素数配列のダミーデータ
     */
    public void setDummyResults(double[] dummyPowerSpectrum, Complex[] dummyComplexResult) {
        this.dummyPowerSpectrum = dummyPowerSpectrum;
        this.dummyComplexResult = dummyComplexResult;
        this.dummyRecalculatedPowerSpectrumData = dummyPowerSpectrum;
    }
    
    /**
     * すべてのダミーデータを設定するメソッド
     */
    public void setAllDummyData(double[] initialOriginData, double[] powerSpectrum, 
                               Complex[] complexResult, double[] ifftResult, 
                               double[] recalculatedPowerSpectrum) {
        this.dummyInitialOriginData = initialOriginData;
        this.dummyPowerSpectrum = powerSpectrum;
        this.dummyComplexResult = complexResult;
        this.dummyIfftResultData = ifftResult;
        this.dummyRecalculatedPowerSpectrumData = recalculatedPowerSpectrum;
    }

    /**
     * 計算のトリガーとなるメソッド。
     * 実際の計算は行わず、設定されたダミーデータをセットしてイベントを発火させる。
     * @param originData 元の信号データ
     */
    public void setCalculatedData(double[] originData) {
        // 実際のFFT計算は行わない
        
        // プロパティ変更イベントを発火してビューに更新を通知
        firePropertyChange("1dData", null, this.dummyPowerSpectrum);
    }

    /**
     * 計算結果（パワースペクトル）を返すメソッド。
     * 実際の計算結果ではなく、テスト用に設定されたダミーデータを返す。
     * @return ダミーのパワースペクトルデータ
     */
    public double[] getCalculatedData() {
        return this.dummyPowerSpectrum;
    }

    /**
     * 計算結果（複素数配列）を返すメソッド。
     * @return ダミーの複素数配列データ
     */
    public Complex[] getComplexResult() {
        return this.dummyComplexResult;
    }

    /**
     * 初期の元データを返すメソッド
     * @return ダミーの初期元データ
     */
    @Override
    public double[] getInitialOriginData() {
        return this.dummyInitialOriginData;
    }

    /**
     * 初期計算されたパワースペクトルデータを返すメソッド
     * @return ダミーの初期パワースペクトルデータ
     */
    @Override
    public double[] getInitialCalculatedPowerSpectrumData() {
        return this.dummyPowerSpectrum;
    }

    /**
     * IFFT結果データを返すメソッド
     * @return ダミーのIFFT結果データ
     */
    @Override
    public double[] getIfftResultData() {
        return this.dummyIfftResultData;
    }

    /**
     * 再計算されたパワースペクトルデータを返すメソッド
     * @return ダミーの再計算パワースペクトルデータ
     */
    @Override
    public double[] getRecalculatedPowerSpectrumData() {
        return this.dummyRecalculatedPowerSpectrumData;
    }

    // --- 計算ロジックはすべて空でオーバーライドし、実行されないようにする ---
    // 注意: これらのメソッドは親クラスに存在しないため、オーバーライドではありません

    public void fft(int start, int n) {
        // 何もしない
    }

    public void bitReverseReorder() {
        // 何もしない
    }

    public void ifft() {
        // 何もしない
    }
}