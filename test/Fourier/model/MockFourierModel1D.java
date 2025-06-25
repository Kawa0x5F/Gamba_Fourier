package Fourier.model;

import Fourier.Complex;
import Fourier.model.FourierModel1D;

/**
 * FourierView1DをテストするためのFourierModel1Dのモッククラス（修正版）
 * 実際の計算ロジックをバイパスし、テスト用のデータを返す。
 */
public class MockFourierModel1D extends FourierModel1D {

    private double[] dummyPowerSpectrum;
    private Complex[] dummyComplexResult;

    /**
     * テストで期待される結果データをあらかじめモックに設定しておくメソッド
     * @param dummyPowerSpectrum getCalculatedData()が返すパワースペクトルのダミーデータ
     * @param dummyComplexResult getComplexResult()が返す複素数配列のダミーデータ
     */
    public void setDummyResults(double[] dummyPowerSpectrum, Complex[] dummyComplexResult) {
        this.dummyPowerSpectrum = dummyPowerSpectrum;
        this.dummyComplexResult = dummyComplexResult;
    }

    /**
     * 計算のトリガーとなるメソッド。
     * 実際の計算は行わず、設定されたダミーデータをセットしてイベントを発火させる。
     * @param originData 元の信号データ
     */
    @Override
    public void setCalculatedData(double[] originData) {
        // 実際のFFT計算は行わない
        
        // setDummyResultsで設定されたダミーデータを、あたかも計算したかのようにフィールドに設定
        super.setComplexOriginData(this.dummyComplexResult != null ? this.dummyComplexResult.clone() : null);
        
        // 親クラスのフィールド（calculatedData）に直接アクセスできないため、
        // このような形でダミーデータを設定する。
        // もし親クラスにセッターがあればそれを使用する。
        double[] oldData = super.getCalculatedData();
        
        // ここで private な calculatedData を直接書き換える代わりに、
        // getCalculatedData() がダミーデータを返すように振る舞いを変更する。
        // 下記のgetCalculatedData()のオーバーライドを参照。

        // Viewに変更した通知を送る（プロパティ名は "1dData" に合わせる）
        firePropertyChange("1dData", oldData, this.dummyPowerSpectrum);
    }

    /**
     * 計算結果（パワースペクトル）を返すメソッド。
     * 実際の計算結果ではなく、テスト用に設定されたダミーデータを返す。
     * @return ダミーのパワースペクトルデータ
     */
    @Override
    public double[] getCalculatedData() {
        return this.dummyPowerSpectrum;
    }

    /**
     * 計算結果（複素数配列）を返すメソッド。
     * @return ダミーの複素数配列データ
     */
    @Override
    public Complex[] getComplexResult() {
        return this.dummyComplexResult;
    }

    // --- 計算ロジックはすべて空でオーバーライドし、実行されないようにする ---

    @Override
    public void fft(int start, int n) {
        // 何もしない
    }

    @Override
    public void bitReverseReorder() {
        // 何もしない
    }

    @Override
    public void ifft() {
        // 何もしない
    }
}