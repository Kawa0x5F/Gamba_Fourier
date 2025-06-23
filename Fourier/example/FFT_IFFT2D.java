package Fourier.example;
import Fourier.Complex;
import Fourier.model.FourierModel;
import Fourier.view.FourierView1D;

public class FFT_IFFT2D extends FourierModel{

    private Complex[][] complexOriginData;
    private double[][] calculatedData;

     public double[][] getCalculatedData() {
        return calculatedData; 
    }

    // 2D FFT（行FFT → 転置 → 行FFT → 転置）
    public static void fft2D() {
        // 行方向のFFT
        for (int i = 0; i < height; i++) {
            FFT_IFFT.fft(0, 20);
        }

        // 転置して列→行に
        Complex[][] transposed = transpose(data);

        // 列（＝転置後の行）方向FFT
        for (int i = 0; i < width; i++) {
            FFT_IFFT.fft(transposed[i]);
        }

        // 再転置して元の形に戻す
        Complex[][] result = transpose(transposed);

        // 結果をdataに戻す（参照先更新）
        for (int i = 0; i < height; i++) {
            System.arraycopy(result[i], 0, data[i], 0, width);
        }
    }

    // 2D IFFT（行IFFT → 転置 → 行IFFT → 再転置）
    public static void ifft2D(Complex[][] data) {
        int height = data.length;
        int width = data[0].length;

        // 行方向のIFFT
        for (int i = 0; i < height; i++) {
            FFT_IFFT.ifft(data[i]);
        }

        // 転置
        Complex[][] transposed = transpose(data);

        // 列（＝転置後の行）方向IFFT
        for (int i = 0; i < width; i++) {
            FFT_IFFT.ifft(transposed[i]);
        }

        // 再転置
        Complex[][] result = transpose(transposed);

        // 結果をdataに戻す
        for (int i = 0; i < height; i++) {
            System.arraycopy(result[i], 0, data[i], 0, width);
        }
    }

    // 転置
    public static Complex[][] transpose(Complex[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Complex[][] transposed = new Complex[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }

        return transposed;
    }

    // テスト用
    public static void testFFT2D() {
        int N = 4;
        Complex[][] input = new Complex[N][N];

        // 入力：対角線に1、それ以外0
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                input[i][j] = new Complex((i == j) ? 1 : 0, 0);
            }
        }

        System.out.println("=== Original ===");
        printMatrix(input);

        fft2D(input);
        System.out.println("\n=== FFT 2D ===");
        printMatrix(input);

        ifft2D(input);
        System.out.println("\n=== IFFT 2D ===");
        printMatrix(input);
    }

    private static void printMatrix(Complex[][] matrix) {
        for (Complex[] row : matrix) {
            for (Complex c : row) {
                System.out.print(c + "\t");
            }
            System.out.println();
        }
    }
}
