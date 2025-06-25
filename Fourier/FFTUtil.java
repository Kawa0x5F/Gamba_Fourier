// 新規ファイル: Fourier/FFTUtil.java
package Fourier;

import Fourier.model.FourierModel1D; // bitReverseなどのヘルパーメソッドのためにインポート

public class FFTUtil {

    // bitReverseは静的メソッドとしてFFTUtilに含める
    public static int bitReverse(int x, int bits) {
        int y = 0;
        for (int i = 0; i < bits; i++) {
            y = (y << 1) | (x & 1);
            x >>= 1;
        }
        return y;
    }

    // bitReverseReorderは静的メソッドとしてFFTUtilに含める
    public static void bitReverseReorder(Complex[] data) {
        Integer N = data.length;
        int bits = Integer.numberOfTrailingZeros(N);
        for (int i = 0; i < N; i++) {
            int j = bitReverse(i, bits);
            if (i < j) {
                Complex temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
        }
    }

    // FFTは静的メソッドとしてFFTUtilに含める
    public static void fft(Complex[] data, int start, int n) {
        if (n == 1) return;
        int half = n / 2;
        for (int k = 0; k < half; k++) {
            int i = start + k;
            int j = i + half;
            double angle = -2 * Math.PI * k / n;
            Complex w = new Complex(Math.cos(angle), Math.sin(angle));
            Complex t = data[j];
            Complex u = data[i];
            data[i] = u.add(t);
            data[j] = w.mul(u.sub(t));
        }
        fft(data, start, half);
        fft(data, start + half, half);
    }

    // IFFTは静的メソッドとしてFFTUtilに含める
    public static void ifft(Complex[] data) {
        // 共役複素数化
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].conjugate();
        }

        // FFTを実行
        fft(data, 0, data.length);
        bitReverseReorder(data);

        // 再び共役複素数化して正規化
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].conjugate().scale(1.0 / data.length);
        }
    }
}