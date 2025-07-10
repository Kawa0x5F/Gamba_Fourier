package Fourier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FFTUtilクラスの単体テストクラス
 * FFT、IFFT、ビット反転、シフト操作等のテストを行う
 * 
 * @author Generated Test
 * @see FFTUtil
 */
class FFTUtilTest {

    private static final double DELTA = 1e-10;
    private Complex[] testData4;
    private Complex[] testData8;

    @BeforeEach
    void setUp() {
        // 4要素のテストデータ: [1, 2, 3, 4]
        testData4 = new Complex[]{
            new Complex(1, 0),
            new Complex(2, 0),
            new Complex(3, 0),
            new Complex(4, 0)
        };

        // 8要素のテストデータ: [1, 2, 3, 4, 5, 6, 7, 8]
        testData8 = new Complex[]{
            new Complex(1, 0), new Complex(2, 0), new Complex(3, 0), new Complex(4, 0),
            new Complex(5, 0), new Complex(6, 0), new Complex(7, 0), new Complex(8, 0)
        };
    }

    @Nested
    @DisplayName("ビット反転のテスト")
    class BitReverseTest {

        @Test
        @DisplayName("ビット反転が正常に動作する")
        void testBitReverse() {
            assertEquals(0, FFTUtil.bitReverse(0, 3)); // 000 -> 000
            assertEquals(4, FFTUtil.bitReverse(1, 3)); // 001 -> 100
            assertEquals(2, FFTUtil.bitReverse(2, 3)); // 010 -> 010
            assertEquals(6, FFTUtil.bitReverse(3, 3)); // 011 -> 110
            assertEquals(1, FFTUtil.bitReverse(4, 3)); // 100 -> 001
            assertEquals(5, FFTUtil.bitReverse(5, 3)); // 101 -> 101
            assertEquals(3, FFTUtil.bitReverse(6, 3)); // 110 -> 011
            assertEquals(7, FFTUtil.bitReverse(7, 3)); // 111 -> 111
        }

        @Test
        @DisplayName("2ビットのビット反転")
        void testBitReverse2Bits() {
            assertEquals(0, FFTUtil.bitReverse(0, 2)); // 00 -> 00
            assertEquals(2, FFTUtil.bitReverse(1, 2)); // 01 -> 10
            assertEquals(1, FFTUtil.bitReverse(2, 2)); // 10 -> 01
            assertEquals(3, FFTUtil.bitReverse(3, 2)); // 11 -> 11
        }

        @Test
        @DisplayName("ビット反転並べ替えが正常に動作する")
        void testBitReverseReorder() {
            Complex[] data = {
                new Complex(0, 0), new Complex(1, 0), 
                new Complex(2, 0), new Complex(3, 0)
            };
            Complex[] expected = {
                new Complex(0, 0), new Complex(2, 0), 
                new Complex(1, 0), new Complex(3, 0)
            };

            FFTUtil.bitReverseReorder(data);

            for (int i = 0; i < data.length; i++) {
                assertEquals(expected[i].getReal(), data[i].getReal(), DELTA);
                assertEquals(expected[i].getImaginary(), data[i].getImaginary(), DELTA);
            }
        }
    }

    @Nested
    @DisplayName("FFTのテスト")
    class FFTTest {

        @Test
        @DisplayName("4点FFTが正常に動作する")
        void testFFT4Point() {
            Complex[] data = testData4.clone();
            
            // FFTを実行
            FFTUtil.fft(data, 0, data.length);
            FFTUtil.bitReverseReorder(data);

            // 期待される結果: [10, -2+2i, -2, -2-2i]
            Complex[] expected = {
                new Complex(10, 0),
                new Complex(-2, 2),
                new Complex(-2, 0),
                new Complex(-2, -2)
            };

            for (int i = 0; i < data.length; i++) {
                assertEquals(expected[i].getReal(), data[i].getReal(), DELTA, 
                    "Index " + i + " の実数部が一致しません");
                assertEquals(expected[i].getImaginary(), data[i].getImaginary(), DELTA, 
                    "Index " + i + " の虚数部が一致しません");
            }
        }

        @Test
        @DisplayName("単一要素のFFTは元の値と同じ")
        void testFFTSingleElement() {
            Complex[] data = {new Complex(5, 3)};
            Complex[] original = data.clone();
            
            FFTUtil.fft(data, 0, 1);
            
            assertEquals(original[0].getReal(), data[0].getReal(), DELTA);
            assertEquals(original[0].getImaginary(), data[0].getImaginary(), DELTA);
        }

        @Test
        @DisplayName("2要素のFFTが正常に動作する")
        void testFFT2Point() {
            Complex[] data = {new Complex(1, 0), new Complex(2, 0)};
            
            FFTUtil.fft(data, 0, data.length);
            FFTUtil.bitReverseReorder(data);

            // 期待される結果: [3, -1]
            assertEquals(3.0, data[0].getReal(), DELTA);
            assertEquals(0.0, data[0].getImaginary(), DELTA);
            assertEquals(-1.0, data[1].getReal(), DELTA);
            assertEquals(0.0, data[1].getImaginary(), DELTA);
        }
    }

    @Nested
    @DisplayName("IFFTのテスト")
    class IFFTTest {

        @Test
        @DisplayName("IFFT(FFT(data)) = dataが成立する")
        void testIFFTInverse() {
            Complex[] original = testData8.clone();
            Complex[] data = testData8.clone();

            // FFT -> IFFT
            FFTUtil.fft(data, 0, data.length);
            FFTUtil.bitReverseReorder(data);
            FFTUtil.ifft(data);

            // 元のデータと比較
            for (int i = 0; i < data.length; i++) {
                assertEquals(original[i].getReal(), data[i].getReal(), DELTA,
                    "Index " + i + " の実数部が一致しません");
                assertEquals(original[i].getImaginary(), data[i].getImaginary(), DELTA,
                    "Index " + i + " の虚数部が一致しません");
            }
        }

        @Test
        @DisplayName("IFFTの正規化が正常に動作する")
        void testIFFTNormalization() {
            // 全て1の配列をFFTしてからIFFTすると元に戻るかテスト
            Complex[] data = new Complex[4];
            for (int i = 0; i < 4; i++) {
                data[i] = new Complex(1, 0);
            }

            FFTUtil.fft(data, 0, data.length);
            FFTUtil.bitReverseReorder(data);
            FFTUtil.ifft(data);

            // 全て1に戻っているはず
            for (int i = 0; i < data.length; i++) {
                assertEquals(1.0, data[i].getReal(), DELTA);
                assertEquals(0.0, data[i].getImaginary(), DELTA);
            }
        }
    }

    @Nested
    @DisplayName("シフト操作のテスト")
    class ShiftTest {

        @Test
        @DisplayName("1次元実数配列のシフトが正常に動作する")
        void testShift1DReal() {
            double[] data = {1.0, 2.0, 3.0, 4.0};
            double[] expected = {3.0, 4.0, 1.0, 2.0};
            
            FFTUtil.shift(data);
            
            assertArrayEquals(expected, data, DELTA);
        }

        @Test
        @DisplayName("1次元複素数配列のシフトが正常に動作する")
        void testShift1DComplex() {
            Complex[] data = {
                new Complex(1, 1), new Complex(2, 2), 
                new Complex(3, 3), new Complex(4, 4)
            };
            Complex[] expected = {
                new Complex(3, 3), new Complex(4, 4), 
                new Complex(1, 1), new Complex(2, 2)
            };
            
            FFTUtil.shift(data);
            
            for (int i = 0; i < data.length; i++) {
                assertEquals(expected[i].getReal(), data[i].getReal(), DELTA);
                assertEquals(expected[i].getImaginary(), data[i].getImaginary(), DELTA);
            }
        }

        @Test
        @DisplayName("2次元実数配列のシフトが正常に動作する")
        void testShift2DReal() {
            double[][] data = {
                {1.0, 2.0}, 
                {3.0, 4.0}
            };
            double[][] expected = {
                {4.0, 3.0}, 
                {2.0, 1.0}
            };
            
            FFTUtil.shift(data);
            
            for (int i = 0; i < data.length; i++) {
                assertArrayEquals(expected[i], data[i], DELTA);
            }
        }

        @Test
        @DisplayName("2次元複素数配列のシフトが正常に動作する")
        void testShift2DComplex() {
            Complex[][] data = {
                {new Complex(1, 1), new Complex(2, 2)}, 
                {new Complex(3, 3), new Complex(4, 4)}
            };
            Complex[][] expected = {
                {new Complex(4, 4), new Complex(3, 3)}, 
                {new Complex(2, 2), new Complex(1, 1)}
            };
            
            FFTUtil.shift(data);
            
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    assertEquals(expected[i][j].getReal(), data[i][j].getReal(), DELTA);
                    assertEquals(expected[i][j].getImaginary(), data[i][j].getImaginary(), DELTA);
                }
            }
        }

        @Test
        @DisplayName("4x4配列のシフトが正常に動作する")
        void testShift4x4Real() {
            double[][] data = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
            };
            
            FFTUtil.shift(data);
            
            // 象限の入れ替えが正しく行われているかチェック
            // 左上と右下、右上と左下が交換される
            double[][] expected = {
                {11, 12, 9, 10},
                {15, 16, 13, 14},
                {3, 4, 1, 2},
                {7, 8, 5, 6}
            };
            
            for (int i = 0; i < data.length; i++) {
                assertArrayEquals(expected[i], data[i], DELTA);
            }
        }
    }

    @Nested
    @DisplayName("数学的性質のテスト")
    class MathematicalPropertiesTest {

        @Test
        @DisplayName("線形性: FFT(a*x + b*y) = a*FFT(x) + b*FFT(y)")
        void testFFTLinearity() {
            Complex[] x = {new Complex(1, 0), new Complex(2, 0), new Complex(3, 0), new Complex(4, 0)};
            Complex[] y = {new Complex(2, 0), new Complex(1, 0), new Complex(4, 0), new Complex(3, 0)};
            double a = 2.0, b = 3.0;

            // ax + by を計算
            Complex[] combined = new Complex[x.length];
            for (int i = 0; i < x.length; i++) {
                combined[i] = x[i].scale(a).add(y[i].scale(b));
            }

            // FFT(ax + by)を計算
            Complex[] combinedFFT = combined.clone();
            FFTUtil.fft(combinedFFT, 0, combinedFFT.length);
            FFTUtil.bitReverseReorder(combinedFFT);

            // a*FFT(x) + b*FFT(y)を計算
            Complex[] xFFT = x.clone();
            FFTUtil.fft(xFFT, 0, xFFT.length);
            FFTUtil.bitReverseReorder(xFFT);

            Complex[] yFFT = y.clone();
            FFTUtil.fft(yFFT, 0, yFFT.length);
            FFTUtil.bitReverseReorder(yFFT);

            Complex[] linearCombination = new Complex[x.length];
            for (int i = 0; i < x.length; i++) {
                linearCombination[i] = xFFT[i].scale(a).add(yFFT[i].scale(b));
            }

            // 結果が一致するかチェック
            for (int i = 0; i < x.length; i++) {
                assertEquals(linearCombination[i].getReal(), combinedFFT[i].getReal(), DELTA);
                assertEquals(linearCombination[i].getImaginary(), combinedFFT[i].getImaginary(), DELTA);
            }
        }

        @Test
        @DisplayName("パーセバルの定理: sum(|x|²) = (1/N) * sum(|X|²)")
        void testParsevalTheorem() {
            Complex[] data = testData4.clone();
            
            // 元データのパワー計算
            double originalPower = 0;
            for (Complex c : data) {
                originalPower += c.magnitude() * c.magnitude();
            }

            // FFT実行
            FFTUtil.fft(data, 0, data.length);
            FFTUtil.bitReverseReorder(data);

            // FFT結果のパワー計算
            double fftPower = 0;
            for (Complex c : data) {
                fftPower += c.magnitude() * c.magnitude();
            }

            // パーセバルの定理の確認
            assertEquals(originalPower, fftPower / data.length, DELTA);
        }
    }
}
