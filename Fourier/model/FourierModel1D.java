package Fourier.model;

import Fourier.Complex;
import Fourier.view.FourierView1D;

public class FourierModel1D extends FourierModel {

	private FourierView1D fourierView1D;

	/**
     * 高速フーリエ変換 (FFT) を実行するメソッド (クーリー・テューキーのアルゴリズムに基づく再帰的実装)
     *
     * @param x 入力となる複素数の配列。要素数は2のべき乗である必要があります。
     * @return FFT変換後の複素数の配列。
     * @throws IllegalArgumentException 入力配列の長さが0の場合、または2のべき乗でない場合。
     */
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // 基底ケース: N=1
        if (N == 1) {
            return new Complex[]{x[0]};
        }

        // 入力長が0または2のべき乗でない場合のチェック
        if (N == 0) {
            throw new IllegalArgumentException("入力配列の長さは0であってはなりません。");
        }
        if ((N & (N - 1)) != 0) { // Nが2のべき乗かどうかのビット演算チェック
            throw new IllegalArgumentException("入力配列の長さは2のべき乗である必要があります。現在の長さ: " + N);
        }

        // 偶数番目の要素と奇数番目の要素に分割
        Complex[] evenTerms = new Complex[N / 2];
        Complex[] oddTerms = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++) {
            evenTerms[k] = x[2 * k];
            oddTerms[k] = x[2 * k + 1];
        }

        // 再帰的にFFTを適用
        Complex[] fftEven = fft(evenTerms);
        Complex[] fftOdd = fft(oddTerms);

        // 結果を格納する配列
        Complex[] result = new Complex[N];

        // 結合ステップ: X_k = FFT_even_k + W_N^k * FFT_odd_k
        //              X_{k+N/2} = FFT_even_k - W_N^k * FFT_odd_k
        for (int k = 0; k < N / 2; k++) {
            double angle = -2 * Math.PI * k / N;
            Complex Wk = Complex.exp(new Complex(0, angle)); // 回転因子 W_N^k = e^(-2πik/N)
            Complex term = Wk.mul(fftOdd[k]);

            result[k]         = fftEven[k].add(term);
            result[k + N / 2] = fftEven[k].sub(term);
        }
        return result;
    }
}
