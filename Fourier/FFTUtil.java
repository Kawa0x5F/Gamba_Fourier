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

    /**
     * 1次元配列のFFT結果をシフトし、直流成分が中央に来るように並べ替える
     * @param data シフト対象の配列
     */
    public static void shift(double[] data) {
        int half = data.length / 2;
        for (int i = 0; i < half; i++) {
            double temp = data[i];
            data[i] = data[i + half];
            data[i + half] = temp;
        }
    }
    
    /**
     * 1次元配列のFFT結果をシフトし、直流成分が中央に来るように並べ替える
     * @param data シフト対象の配列
     */
    public static void shift(Complex[] data) {
        int half = data.length / 2;
        for (int i = 0; i < half; i++) {
            Complex temp = data[i];
            data[i] = data[i + half];
            data[i + half] = temp;
        }
    }

    /**
     * 2次元配列のFFT結果をシフトし、直流成分が中央に来るように象限を入れ替える
     * @param data シフト対象の配列
     */
    public static void shift(double[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        int halfRows = rows / 2;
        int halfCols = cols / 2;

        for (int r = 0; r < halfRows; r++) {
            for (int c = 0; c < halfCols; c++) {
                // 第1象限(左上)と第3象限(右下)を交換
                double temp = data[r][c];
                data[r][c] = data[r + halfRows][c + halfCols];
                data[r + halfRows][c + halfCols] = temp;

                // 第2象限(右上)と第4象限(左下)を交換
                temp = data[r][c + halfCols];
                data[r][c + halfCols] = data[r + halfRows][c];
                data[r + halfRows][c] = temp;
            }
        }
    }
    
    /**
     * 2次元配列のFFT結果をシフトし、直流成分が中央に来るように象限を入れ替える
     * @param data シフト対象の配列
     */
    public static void shift(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        int halfRows = rows / 2;
        int halfCols = cols / 2;

        for (int r = 0; r < halfRows; r++) {
            for (int c = 0; c < halfCols; c++) {
                // 第1象限(左上)と第3象限(右下)を交換
                Complex temp = data[r][c];
                data[r][c] = data[r + halfRows][c + halfCols];
                data[r + halfRows][c + halfCols] = temp;

                // 第2象限(右上)と第4象限(左下)を交換
                temp = data[r][c + halfCols];
                data[r][c + halfCols] = data[r + halfRows][c];
                data[r + halfRows][c] = temp;
            }
        }
    }
}