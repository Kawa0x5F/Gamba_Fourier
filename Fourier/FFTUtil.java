package Fourier;

/**
 * 高速フーリエ変換（FFT）のユーティリティクラス。
 * FFT、逆FFT、ビット反転、シフト操作などを提供します。
 */
public class FFTUtil {

    /**
     * 指定されたビット数で整数のビット反転を行います。
     * @param x ビット反転する整数
     * @param bits ビット数
     * @return ビット反転された整数
     */
    public static int bitReverse(int x, int bits) {
        int y = 0;
        for (int i = 0; i < bits; i++) {
            y = (y << 1) | (x & 1);
            x >>= 1;
        }
        return y;
    }

    /**
     * データ配列をビット反転順序に並び替えます。
     * @param data 並び替える複素数配列
     */
    public static void bitReverseReorder(Complex[] data) {
        int N = data.length;
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

    /**
     * 再帰的FFTメソッド
     * 事前計算された回転因子(twiddles)テーブルを使用し、
     * ミュータブルなComplexオブジェクトでインプレース計算を行います。
     * @param data FFTを適用する複素数配列
     * @param start 開始インデックス
     * @param n 処理するデータ数
     * @param twiddles 事前計算された回転因子テーブル
     */
    public static void fftRecursive(Complex[] data, int start, int n, Complex[] twiddles) {
        if (n == 1) return;
        int half = n / 2;
        int stride = twiddles.length * 2 / n; // 再帰の深さに応じたストライドを計算

        for (int k = 0; k < half; k++) {
            int i = start + k;
            int j = i + half;
            
            // 事前計算した回転因子テーブルを使用
            Complex w = twiddles[k * stride];
            
            Complex u = data[i];
            Complex t = data[j];

            // ミュータブルなComplexメソッドを使用してオブジェクト生成を回避
            // バタフライ演算:
            // data[i] = u + t
            // data[j] = w * (u - t)
            double u_real = u.getReal();
            double u_imag = u.getImaginary();
            double t_real = t.getReal();
            double t_imag = t.getImaginary();

            double diff_real = u_real - t_real;
            double diff_imag = u_imag - t_imag;

            // data[i]の更新
            u.set(u_real + t_real, u_imag + t_imag);
            
            // data[j]の更新 (w * diff)
            t.set(
                w.getReal() * diff_real - w.getImaginary() * diff_imag,
                w.getReal() * diff_imag + w.getImaginary() * diff_real
            );
        }
        fftRecursive(data, start, half, twiddles);
        fftRecursive(data, start + half, half, twiddles);
    }
    
    /**
     * 順変換FFTの呼び出し用ラッパー
     * @param data FFTを適用する複素数配列
     * @param twiddles 事前計算された回転因子テーブル
     */
    public static void fft(Complex[] data, Complex[] twiddles) {
        fftRecursive(data, 0, data.length, twiddles);
        bitReverseReorder(data);
    }

    /**
     * 逆FFT（IFFT）メソッド
     * 共役化ループを廃止し、逆回転因子で直接計算し、最後にデータ数でスケーリングします。
     * @param data IFFTを適用する複素数配列
     * @param invTwiddles 逆変換用の回転因子テーブル
     */
    public static void ifft(Complex[] data, Complex[] invTwiddles) {
        // 逆変換は、順変換と同じアルゴリズムで逆回転因子を使うだけ
        fftRecursive(data, 0, data.length, invTwiddles);
        bitReverseReorder(data);

        // 最後にデータ数でスケーリング
        double scale = 1.0 / data.length;
        for (int i = 0; i < data.length; i++) {
            data[i].scaleInPlace(scale);
        }
    }

    /**
     * 1次元実数配列のシフト操作を行います。
     * @param data シフトする実数配列
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
     * 1次元複素数配列のシフト操作を行います。
     * @param data シフトする複素数配列
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
     * 2次元実数配列のシフト操作を行います。
     * @param data シフトする2次元実数配列
     */
    public static void shift(double[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        int halfRows = rows / 2;
        int halfCols = cols / 2;
        for (int r = 0; r < halfRows; r++) {
            for (int c = 0; c < halfCols; c++) {
                double temp = data[r][c];
                data[r][c] = data[r + halfRows][c + halfCols];
                data[r + halfRows][c + halfCols] = temp;
                temp = data[r][c + halfCols];
                data[r][c + halfCols] = data[r + halfRows][c];
                data[r + halfRows][c] = temp;
            }
        }
    }
    
    /**
     * 2次元複素数配列のシフト操作を行います。
     * @param data シフトする2次元複素数配列
     */
    public static void shift(Complex[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        int halfRows = rows / 2;
        int halfCols = cols / 2;
        for (int r = 0; r < halfRows; r++) {
            for (int c = 0; c < halfCols; c++) {
                Complex temp = data[r][c];
                data[r][c] = data[r + halfRows][c + halfCols];
                data[r + halfRows][c + halfCols] = temp;
                temp = data[r][c + halfCols];
                data[r][c + halfCols] = data[r + halfRows][c];
                data[r + halfRows][c] = temp;
            }
        }
    }
}