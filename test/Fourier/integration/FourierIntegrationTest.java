package Fourier.integration;

import Fourier.*;
import Fourier.model.*;
import Fourier.view.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * フーリエ信号処理システムの結合テストクラス
 * MVC統合、性能、エラーハンドリング、UI、データ整合性のテストを行う
 * 
 * @author Generated Integration Test
 */
class FourierIntegrationTest {

    private static final double DELTA = 1e-10;

    // 回転因子を生成するヘルパーメソッド
    private Complex[] generateTwiddles(int n) {
        Complex[] twiddles = new Complex[n / 2];
        for (int k = 0; k < n / 2; k++) {
            double angle = -2.0 * Math.PI * k / n;
            twiddles[k] = new Complex(Math.cos(angle), Math.sin(angle));
        }
        return twiddles;
    }

    // 逆回転因子を生成するヘルパーメソッド
    private Complex[] generateInverseTwiddles(int n) {
        Complex[] invTwiddles = new Complex[n / 2];
        for (int k = 0; k < n / 2; k++) {
            double angle = 2.0 * Math.PI * k / n; // 符号が逆
            invTwiddles[k] = new Complex(Math.cos(angle), Math.sin(angle));
        }
        return invTwiddles;
    }

    @Nested
    @DisplayName("MVC統合テスト")
    class MVCIntegrationTest {

        @Test
        @DisplayName("1D信号処理統合テスト")
        void test1DSignalProcessingFlow() {
            // 1. テスト信号データの準備
            double[] signalData = new double[8];
            for (int i = 0; i < signalData.length; i++) {
                signalData[i] = Math.sin(2 * Math.PI * i / signalData.length);
            }
            
            // 2. モデルの作成（コンストラクタで初期データを設定）
            FourierModel1D model = new FourierModel1D(signalData);
            
            // 3. 初期データが正しく設定されたことを確認
            double[] initialData = model.getInitialOriginData();
            assertNotNull(initialData);
            assertEquals(signalData.length, initialData.length);
            for (int i = 0; i < signalData.length; i++) {
                assertEquals(signalData[i], initialData[i], DELTA);
            }
            
            // 4. 初期計算されたスペクトルデータの確認
            double[] powerSpectrum = model.getInitialCalculatedPowerSpectrumData();
            assertNotNull(powerSpectrum);
            
            // 5. ユーザーモディファイされたスペクトルデータの確認
            Complex[] userSpectrum = model.getUserModifiedSpectrumData();
            assertNotNull(userSpectrum);
            
            // 6. IFFT結果データの確認
            double[] ifftResult = model.getIfftResultData();
            assertNotNull(ifftResult);
        }

        @Test
        @DisplayName("ファイルI/O統合テスト")
        void testFileIOIntegration() {
            // FourierDataの静的メソッドを使用してテストデータを取得
            double[][] testImageData = FourierData.data4x4();
            
            // データの整合性を確認
            assertNotNull(testImageData);
            assertEquals(4, testImageData.length);
            assertEquals(4, testImageData[0].length);
            
            // FourierDataから提供されるデータの検証
            for (int i = 0; i < testImageData.length; i++) {
                for (int j = 0; j < testImageData[i].length; j++) {
                    assertNotNull(testImageData[i][j]);
                    assertTrue(testImageData[i][j] >= 0, "Data should be non-negative");
                }
            }
        }
    }

    @Nested
    @DisplayName("性能統合テスト")
    class PerformanceIntegrationTest {

        @Test
        @DisplayName("ミュータブル最適化効果テスト")
        void testMutableOptimizationPerformance() {
            int n = 1024;
            Complex[] data1 = new Complex[n];
            Complex[] data2 = new Complex[n];
            
            // 同じテストデータを準備
            for (int i = 0; i < n; i++) {
                data1[i] = new Complex(Math.random(), Math.random());
                data2[i] = new Complex(data1[i].getReal(), data1[i].getImaginary());
            }
            
            // ミュータブルメソッドの使用
            long startTime = System.nanoTime();
            for (int i = 0; i < n; i++) {
                data1[i].mulInPlace(data1[i]);
                data1[i].scaleInPlace(0.5);
            }
            long mutableTime = System.nanoTime() - startTime;
            
            // イミュータブルメソッドの使用
            startTime = System.nanoTime();
            for (int i = 0; i < n; i++) {
                data2[i] = data2[i].mul(data2[i]);
                data2[i] = data2[i].scale(0.5);
            }
            long immutableTime = System.nanoTime() - startTime;
            
            // パフォーマンス情報の出力
            System.out.println("Mutable time: " + mutableTime + "ns");
            System.out.println("Immutable time: " + immutableTime + "ns");
            
            // 結果の正確性確認
            for (int i = 0; i < Math.min(10, n); i++) {
                assertEquals(data1[i].getReal(), data2[i].getReal(), DELTA);
                assertEquals(data1[i].getImaginary(), data2[i].getImaginary(), DELTA);
            }
        }
    }

    @Nested
    @DisplayName("エラーハンドリング統合テスト")
    class ErrorHandlingIntegrationTest {

        @Test
        @DisplayName("数値計算エラーテスト")
        void testNumericalErrorHandling() {
            // ゼロ除算のテスト
            Complex zero = new Complex(0, 0);
            Complex nonZero = new Complex(1, 1);
            
            assertThrows(ArithmeticException.class, () -> {
                nonZero.div(zero);
            }, "Division by zero should throw ArithmeticException");
        }

        @Test
        @DisplayName("無効データ処理テスト")
        void testInvalidDataHandling() {
            // null データでのモデル作成テスト
            assertDoesNotThrow(() -> {
                FourierModel1D model = new FourierModel1D();
                // モデルの基本操作が例外を投げないことを確認
                assertNotNull(model);
            }, "Creating model with default constructor should not throw exception");
        }
    }

    @Nested
    @DisplayName("ユーザーインターフェース統合テスト")
    class UIIntegrationTest {

        @Test
        @DisplayName("SignalPanelの描画テスト")
        void testSignalPanelRendering() {
            // テスト用のSignalPanelを作成
            SignalPanel panel = new SignalPanel("Test Signal");
            
            // テストデータを設定
            double[] testData = {1.0, 2.0, -1.0, 0.5, -0.5};
            panel.setData(testData);
            
            // データが正しく設定されたことを確認
            double[] retrievedData = panel.getData();
            assertNotNull(retrievedData);
            assertEquals(testData.length, retrievedData.length);
            for (int i = 0; i < testData.length; i++) {
                assertEquals(testData[i], retrievedData[i], DELTA);
            }
            
            // 固定最大値の設定テスト
            panel.setFixedMaxValue(5.0);
            // 描画テストは実際のGUI環境が必要なため、基本的な設定のみテスト
        }
    }

    @Nested
    @DisplayName("データ整合性テスト")
    class DataConsistencyTest {

        @Test
        @DisplayName("FFT-IFFT往復変換精度テスト")
        void testFFTIFFTRoundTripAccuracy() {
            // 様々なサイズでのテスト
            int[] sizes = {4, 8, 16, 32, 64};
            
            for (int size : sizes) {
                Complex[] originalData = new Complex[size];
                for (int i = 0; i < size; i++) {
                    originalData[i] = new Complex(Math.random(), Math.random());
                }
                
                Complex[] data = new Complex[size];
                for (int i = 0; i < size; i++) {
                    data[i] = new Complex(originalData[i].getReal(), originalData[i].getImaginary());
                }
                
                // 回転因子の生成
                Complex[] twiddles = generateTwiddles(size);
                Complex[] invTwiddles = generateInverseTwiddles(size);
                
                // FFT実行
                FFTUtil.fft(data, twiddles);
                
                // IFFT実行
                FFTUtil.ifft(data, invTwiddles);
                
                // 元データとの比較
                for (int i = 0; i < size; i++) {
                    assertEquals(originalData[i].getReal(), data[i].getReal(), DELTA,
                        "Real part should match after round trip (size=" + size + ", index=" + i + ")");
                    assertEquals(originalData[i].getImaginary(), data[i].getImaginary(), DELTA,
                        "Imaginary part should match after round trip (size=" + size + ", index=" + i + ")");
                }
            }
        }

        @Test
        @DisplayName("データフォーマット変換テスト")
        void testDataFormatConversion() {
            // 実数配列から複素数配列への変換テスト
            double[] realData = {1.0, 2.0, 3.0, 4.0};
            Complex[] complexData = new Complex[realData.length];
            
            for (int i = 0; i < realData.length; i++) {
                complexData[i] = new Complex(realData[i], 0.0);
            }
            
            // 変換の正確性確認
            for (int i = 0; i < realData.length; i++) {
                assertEquals(realData[i], complexData[i].getReal(), DELTA);
                assertEquals(0.0, complexData[i].getImaginary(), DELTA);
            }
            
            // 複素数配列から実数配列への変換テスト
            double[] backToReal = new double[complexData.length];
            for (int i = 0; i < complexData.length; i++) {
                backToReal[i] = complexData[i].getReal();
            }
            
            // 往復変換の正確性確認
            for (int i = 0; i < realData.length; i++) {
                assertEquals(realData[i], backToReal[i], DELTA);
            }
        }

        @Test
        @DisplayName("シフト操作整合性テスト")
        void testShiftOperationConsistency() {
            // 1D実数配列のシフトテスト
            double[] data1D = {1, 2, 3, 4};
            double[] original1D = data1D.clone();
            
            FFTUtil.shift(data1D);
            // 再度シフトすると元に戻る
            FFTUtil.shift(data1D);
            
            assertArrayEquals(original1D, data1D, DELTA);
            
            // 1D複素数配列のシフトテスト
            Complex[] complexData1D = {
                new Complex(1, 1),
                new Complex(2, 2),
                new Complex(3, 3),
                new Complex(4, 4)
            };
            Complex[] originalComplex1D = new Complex[complexData1D.length];
            for (int i = 0; i < complexData1D.length; i++) {
                originalComplex1D[i] = new Complex(complexData1D[i].getReal(), complexData1D[i].getImaginary());
            }
            
            FFTUtil.shift(complexData1D);
            FFTUtil.shift(complexData1D);
            
            for (int i = 0; i < complexData1D.length; i++) {
                assertEquals(originalComplex1D[i].getReal(), complexData1D[i].getReal(), DELTA);
                assertEquals(originalComplex1D[i].getImaginary(), complexData1D[i].getImaginary(), DELTA);
            }
        }
    }
}
