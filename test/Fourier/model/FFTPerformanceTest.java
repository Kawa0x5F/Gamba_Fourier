package Fourier.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import Fourier.Complex;
import Fourier.FFTUtil;

/**
 * FFTの再帰実装と非再帰（反復的）実装のパフォーマンスを比較するテストクラス。
 * JUnit 5 を使用します。
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("FFT Implementation Performance Comparison")
public class FFTPerformanceTest {

    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    // テスト用の元データを静的に保持（各テストで同じデータを使用）
    private static Complex[][] testDataR;
    private static Complex[][] testDataG;
    private static Complex[][] testDataB;

    /**
     * すべてのテストの前に一度だけ、テストデータを生成します。
     */
    @BeforeAll
    static void setUp() {
        System.out.println("Generating " + WIDTH + "x" + HEIGHT + " test data...");
        testDataR = createRandomComplexData(HEIGHT, WIDTH);
        testDataG = createRandomComplexData(HEIGHT, WIDTH);
        testDataB = createRandomComplexData(HEIGHT, WIDTH);
        System.out.println("Test data generated.");
    }

    @Test
    @Order(1)
    @DisplayName("2D FFT (Recursive) Performance")
    void testRecursive2DFFT() {
        // 各テストが独立するようにデータをディープコピー
        Complex[][] dataR = deepCopy(testDataR);
        Complex[][] dataG = deepCopy(testDataG);
        Complex[][] dataB = deepCopy(testDataB);

        System.out.println("\n--- Starting Recursive 2D FFT Test ---");
        long startTime = System.nanoTime();

        // 3チャンネルそれぞれに2D FFTを適用
        perform2DFFT_Recursive(dataR);
        perform2DFFT_Recursive(dataG);
        perform2DFFT_Recursive(dataB);

        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Recursive 2D FFT took: " + duration + " ms");

        assertNotNull(dataR); // テストが成功したことを確認
    }

    @Test
    @Order(2)
    @DisplayName("2D FFT (Iterative) Performance")
    void testIterative2DFFT() {
        Complex[][] dataR = deepCopy(testDataR);
        Complex[][] dataG = deepCopy(testDataG);
        Complex[][] dataB = deepCopy(testDataB);

        System.out.println("\n--- Starting Iterative 2D FFT Test ---");
        long startTime = System.nanoTime();

        // 3チャンネルそれぞれに非再帰2D FFTを適用
        perform2DFFT_Iterative(dataR);
        perform2DFFT_Iterative(dataG);
        perform2DFFT_Iterative(dataB);

        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Iterative 2D FFT took: " + duration + " ms");

        assertNotNull(dataR);
    }
    
    @Test
    @Order(3)
    @DisplayName("2D IFFT (Recursive based) Performance")
    void testRecursive2DIFFT() {
        // IFFTの入力として、FFT済みのデータを使用
        Complex[][] dataR = deepCopy(testDataR);
        perform2DFFT_Iterative(dataR); // 事前にFFTしておく

        System.out.println("\n--- Starting Recursive 2D IFFT Test ---");
        long startTime = System.nanoTime();

        perform2DIFFT_Recursive(dataR);

        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Recursive 2D IFFT took: " + duration + " ms");

        assertNotNull(dataR);
    }
    
    @Test
    @Order(4)
    @DisplayName("2D IFFT (Iterative based) Performance")
    void testIterative2DIFFT() {
        // IFFTの入力として、FFT済みのデータを使用
        Complex[][] dataR = deepCopy(testDataR);
        perform2DFFT_Iterative(dataR); // 事前にFFTしておく

        System.out.println("\n--- Starting Iterative 2D IFFT Test ---");
        long startTime = System.nanoTime();

        perform2DIFFT_Iterative(dataR);

        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Iterative 2D IFFT took: " + duration + " ms");

        assertNotNull(dataR);
    }


    // --- パフォーマンス測定用のヘルパーメソッド ---

    private void perform2DFFT_Recursive(Complex[][] data) {
        // 行FFT
        for (Complex[] row : data) {
            FFTUtil.fft(row, 0, WIDTH);
            FFTUtil.bitReverseReorder(row);
        }
        Complex[][] transposed = transpose(data);
        // 列FFT
        for (Complex[] col : transposed) {
            FFTUtil.fft(col, 0, HEIGHT);
            FFTUtil.bitReverseReorder(col);
        }
    }
    
    private void perform2DFFT_Iterative(Complex[][] data) {
        // 行FFT
        for (Complex[] row : data) {
            FFTUtil.fft_iterative(row);
        }
        Complex[][] transposed = transpose(data);
        // 列FFT
        for (Complex[] col : transposed) {
            FFTUtil.fft_iterative(col);
        }
    }
    
    private void perform2DIFFT_Recursive(Complex[][] data) {
        // 行IFFT
        for (Complex[] row : data) {
            FFTUtil.ifft(row);
        }
        Complex[][] transposed = transpose(data);
        // 列IFFT
        for (Complex[] col : transposed) {
            FFTUtil.ifft(col);
        }
    }

    private void perform2DIFFT_Iterative(Complex[][] data) {
        // 行IFFT
        for (Complex[] row : data) {
            FFTUtil.ifft_iterative(row);
        }
        Complex[][] transposed = transpose(data);
        // 列IFFT
        for (Complex[] col : transposed) {
            FFTUtil.ifft_iterative(col);
        }
    }

    // --- テストデータ生成・操作用のユーティリティメソッド ---

    private static Complex[][] createRandomComplexData(int rows, int cols) {
        Complex[][] data = new Complex[rows][cols];
        Random rand = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = new Complex(rand.nextDouble() * 255.0, 0);
            }
        }
        return data;
    }

    private Complex[][] deepCopy(Complex[][] original) {
        if (original == null) {
            return null;
        }
        Complex[][] result = new Complex[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = new Complex[original[i].length];
            for (int j = 0; j < original[i].length; j++) {
                result[i][j] = new Complex(original[i][j].getReal(), original[i][j].getImaginary());
            }
        }
        return result;
    }

    private Complex[][] transpose(Complex[][] m) {
        int rows = m.length;
        int cols = m[0].length;
        Complex[][] t = new Complex[cols][rows];
        for (int j = 0; j < cols; j++) {
            t[j] = new Complex[rows];
            for (int i = 0; i < rows; i++) {
                t[j][i] = m[i][j];
            }
        }
        return t;
    }
}