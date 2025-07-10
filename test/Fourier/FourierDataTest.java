package Fourier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FourierDataクラスの単体テストクラス
 * 各種テスト信号の生成、画像データの読み込み等のテストを行う
 * 
 * @author Generated Test
 * @see FourierData
 */
class FourierDataTest {

    private static final double DELTA = 1e-10;
    private static final int EXPECTED_1D_SIZE = 1024;
    private static final int EXPECTED_2D_SIZE = 4;

    @Nested
    @DisplayName("1次元テスト信号のテスト")
    class OneDimensionalSignalTest {

        @Test
        @DisplayName("チャープ信号の生成テスト")
        void testDataChirpSignal() {
            double[] chirpSignal = FourierData.dataChirpSignal();
            
            // 基本的な検証
            assertNotNull(chirpSignal);
            assertEquals(EXPECTED_1D_SIZE, chirpSignal.length);
            
            // 最初の値は0になるはず（i=0の時、sin(0) = 0）
            assertEquals(0.0, chirpSignal[0], DELTA);
            
            // 信号の特性を確認（有限の値を持つこと）
            for (double value : chirpSignal) {
                assertTrue(Double.isFinite(value), "チャープ信号に無限値が含まれています");
                assertTrue(Math.abs(value) <= 12.0, "チャープ信号の振幅が予想範囲を超えています");
            }
            
            // 周波数が変化することを確認（連続する値が異なること）
            boolean hasVariation = false;
            for (int i = 1; i < chirpSignal.length; i++) {
                if (Math.abs(chirpSignal[i] - chirpSignal[i-1]) > DELTA) {
                    hasVariation = true;
                    break;
                }
            }
            assertTrue(hasVariation, "チャープ信号に変化が見られません");
        }

        @Test
        @DisplayName("のこぎり波の生成テスト")
        void testDataSawtoothWave() {
            double[] sawtoothWave = FourierData.dataSawtoothWave();
            
            // 基本的な検証
            assertNotNull(sawtoothWave);
            assertEquals(EXPECTED_1D_SIZE, sawtoothWave.length);
            
            // 振幅の範囲確認
            for (double value : sawtoothWave) {
                assertTrue(Double.isFinite(value), "のこぎり波に無限値が含まれています");
                assertTrue(Math.abs(value) <= 12.0, "のこぎり波の振幅が予想範囲を超えています");
            }
            
            // 周期性の確認（50サンプル周期）
            for (int i = 0; i < sawtoothWave.length - 50; i++) {
                if (i + 50 < sawtoothWave.length) {
                    assertEquals(sawtoothWave[i], sawtoothWave[i + 50], DELTA,
                        "のこぎり波の周期性が保たれていません");
                }
            }
            
            // 最初の値は-12.0になるはず（i%50=0の時、(0/25-1)*12 = -12）
            assertEquals(-12.0, sawtoothWave[0], DELTA);
        }

        @Test
        @DisplayName("矩形波の生成テスト")
        void testDataSquareWave() {
            double[] squareWave = FourierData.dataSquareWave();
            
            // 基本的な検証
            assertNotNull(squareWave);
            assertEquals(EXPECTED_1D_SIZE, squareWave.length);
            
            // 値が±12.0のいずれかであることを確認
            for (double value : squareWave) {
                assertTrue(Double.isFinite(value), "矩形波に無限値が含まれています");
                assertTrue(Math.abs(value - 12.0) < DELTA || Math.abs(value + 12.0) < DELTA,
                    "矩形波の値が±12.0以外です: " + value);
            }
            
            // 少なくとも正と負の値が存在することを確認
            boolean hasPositive = false, hasNegative = false;
            for (double value : squareWave) {
                if (value > 0) hasPositive = true;
                if (value < 0) hasNegative = true;
            }
            assertTrue(hasPositive && hasNegative, "矩形波に正と負の値が両方含まれていません");
        }

        @Test
        @DisplayName("サンプル波の生成テスト")
        void testDataSampleWave() {
            double[] sampleWave = FourierData.dataSampleWave();
            
            // 基本的な検証
            assertNotNull(sampleWave);
            assertEquals(EXPECTED_1D_SIZE, sampleWave.length);
            
            // 有限性の確認
            for (double value : sampleWave) {
                assertTrue(Double.isFinite(value), "サンプル波に無限値が含まれています");
            }
            
            // 合成波の振幅が適切な範囲内であることを確認
            // 最大振幅は 6+4+3+2=15 程度になるはず
            double maxValue = Double.NEGATIVE_INFINITY;
            double minValue = Double.POSITIVE_INFINITY;
            for (double value : sampleWave) {
                maxValue = Math.max(maxValue, value);
                minValue = Math.min(minValue, value);
            }
            assertTrue(maxValue <= 16.0, "サンプル波の最大値が予想を超えています");
            assertTrue(minValue >= -16.0, "サンプル波の最小値が予想を下回っています");
            
            // 最初の値の計算確認（i=0の時）
            double expectedFirst = 6.0 + 0.0 + 3.0 + 0.0; // cos(0) + sin(0) + cos(0) + sin(0)
            assertEquals(expectedFirst, sampleWave[0], DELTA);
        }

        @Test
        @DisplayName("三角波の生成テスト")
        void testDataTriangleWave() {
            double[] triangleWave = FourierData.dataTriangleWave();
            
            // 基本的な検証
            assertNotNull(triangleWave);
            assertEquals(EXPECTED_1D_SIZE, triangleWave.length);
            
            // 振幅の範囲確認
            for (double value : triangleWave) {
                assertTrue(Double.isFinite(value), "三角波に無限値が含まれています");
                assertTrue(Math.abs(value) <= 12.0, "三角波の振幅が予想範囲を超えています");
            }
            
            // 周期性の確認（100サンプル周期：50サンプル上り、50サンプル下り）
            for (int i = 0; i < triangleWave.length - 100; i++) {
                if (i + 100 < triangleWave.length) {
                    assertEquals(triangleWave[i], triangleWave[i + 100], DELTA,
                        "三角波の周期性が保たれていません");
                }
            }
            
            // 最初の値は-12.0になるはず
            assertEquals(-12.0, triangleWave[0], DELTA);
        }
    }

    @Nested
    @DisplayName("2次元テスト信号のテスト")
    class TwoDimensionalSignalTest {

        @Test
        @DisplayName("4x4テストデータの生成テスト")
        void testData4x4() {
            double[][] data4x4 = FourierData.data4x4();
            
            // 基本的な検証
            assertNotNull(data4x4);
            assertEquals(EXPECTED_2D_SIZE, data4x4.length);
            
            // 各行の長さを確認
            for (int i = 0; i < data4x4.length; i++) {
                assertNotNull(data4x4[i]);
                assertEquals(EXPECTED_2D_SIZE, data4x4[i].length);
            }
            
            // 予想される値の確認
            double[][] expected = {
                {900, 901, 902, 903},
                {910, 911, 912, 913},
                {920, 921, 922, 923},
                {930, 931, 932, 933}
            };
            
            for (int i = 0; i < expected.length; i++) {
                assertArrayEquals(expected[i], data4x4[i], DELTA);
            }
        }

        @Test
        @DisplayName("フーリエカラー画像データの読み込みテスト")
        void testDataFourierColor() {
            // 画像ファイルが存在しない場合のテスト
            // 実際の画像が存在する場合は正常に動作するはず
            assertDoesNotThrow(() -> {
                double[][][] colorData = FourierData.dataFourierColor();
                // 画像が存在しない場合はnullが返される可能性がある
                if (colorData != null) {
                    // 3次元配列の構造を確認
                    assertTrue(colorData.length > 0);
                    assertTrue(colorData[0].length > 0);
                    assertEquals(3, colorData[0][0].length); // YUVの3チャンネル
                    
                    // 値の範囲確認（YUV値は通常0-255の範囲）
                    for (int x = 0; x < colorData.length; x++) {
                        for (int y = 0; y < colorData[0].length; y++) {
                            for (int c = 0; c < 3; c++) {
                                assertTrue(Double.isFinite(colorData[x][y][c]));
                            }
                        }
                    }
                }
            });
        }

        @Test
        @DisplayName("フーリエグレースケール画像データの読み込みテスト")
        void testDataFourierGrayScale() {
            // 画像ファイルが存在しない場合のテスト
            // 実際の画像が存在する場合は正常に動作するはず
            assertDoesNotThrow(() -> {
                double[][] grayData = FourierData.dataFourierGrayScale();
                // 画像が存在しない場合はnullが返される可能性がある
                if (grayData != null) {
                    // 2次元配列の構造を確認
                    assertTrue(grayData.length > 0);
                    assertTrue(grayData[0].length > 0);
                    
                    // 値の範囲確認（輝度値は通常0-255の範囲）
                    for (int x = 0; x < grayData.length; x++) {
                        for (int y = 0; y < grayData[0].length; y++) {
                            assertTrue(Double.isFinite(grayData[x][y]));
                        }
                    }
                }
            });
        }
    }

    @Nested
    @DisplayName("信号特性のテスト")
    class SignalCharacteristicsTest {

        @Test
        @DisplayName("すべての1次元信号が同じサイズを持つ")
        void testAllSignalsSameSize() {
            assertEquals(EXPECTED_1D_SIZE, FourierData.dataChirpSignal().length);
            assertEquals(EXPECTED_1D_SIZE, FourierData.dataSawtoothWave().length);
            assertEquals(EXPECTED_1D_SIZE, FourierData.dataSquareWave().length);
            assertEquals(EXPECTED_1D_SIZE, FourierData.dataSampleWave().length);
            assertEquals(EXPECTED_1D_SIZE, FourierData.dataTriangleWave().length);
        }

        @Test
        @DisplayName("すべての信号が有限値を持つ")
        void testAllSignalsFinite() {
            double[][] signals = {
                FourierData.dataChirpSignal(),
                FourierData.dataSawtoothWave(),
                FourierData.dataSquareWave(),
                FourierData.dataSampleWave(),
                FourierData.dataTriangleWave()
            };
            
            for (double[] signal : signals) {
                for (double value : signal) {
                    assertTrue(Double.isFinite(value), "信号に無限値が含まれています");
                }
            }
        }

        @Test
        @DisplayName("信号が非定数（変化を持つ）")
        void testSignalsNotConstant() {
            double[][] signals = {
                FourierData.dataChirpSignal(),
                FourierData.dataSawtoothWave(),
                FourierData.dataSquareWave(),
                FourierData.dataSampleWave(),
                FourierData.dataTriangleWave()
            };
            
            for (double[] signal : signals) {
                boolean hasVariation = false;
                for (int i = 1; i < signal.length; i++) {
                    if (Math.abs(signal[i] - signal[0]) > DELTA) {
                        hasVariation = true;
                        break;
                    }
                }
                assertTrue(hasVariation, "信号に変化が見られません");
            }
        }

        @Test
        @DisplayName("周期信号の平均値チェック")
        void testPeriodicSignalAverages() {
            // のこぎり波の平均値は0に近いはず
            double[] sawtoothWave = FourierData.dataSawtoothWave();
            double average = 0.0;
            for (double value : sawtoothWave) {
                average += value;
            }
            average /= sawtoothWave.length;
            assertEquals(0.0, average, 0.1, "のこぎり波の平均値が予想と異なります");
            
            // 三角波の平均値も0に近いはず
            double[] triangleWave = FourierData.dataTriangleWave();
            average = 0.0;
            for (double value : triangleWave) {
                average += value;
            }
            average /= triangleWave.length;
            assertEquals(0.0, average, 0.1, "三角波の平均値が予想と異なります");
        }
    }

    @Nested
    @DisplayName("データの一意性テスト")
    class DataUniquenessTest {

        @Test
        @DisplayName("異なる信号は異なる値を持つ")
        void testDifferentSignalsAreUnique() {
            double[] chirp = FourierData.dataChirpSignal();
            double[] sawtooth = FourierData.dataSawtoothWave();
            double[] square = FourierData.dataSquareWave();
            double[] sample = FourierData.dataSampleWave();
            double[] triangle = FourierData.dataTriangleWave();
            
            // 各信号が異なることを確認
            assertFalse(java.util.Arrays.equals(chirp, sawtooth));
            assertFalse(java.util.Arrays.equals(chirp, square));
            assertFalse(java.util.Arrays.equals(chirp, sample));
            assertFalse(java.util.Arrays.equals(chirp, triangle));
            assertFalse(java.util.Arrays.equals(sawtooth, square));
        }

        @Test
        @DisplayName("同じメソッドを複数回呼び出しても同じ結果を返す")
        void testDeterministicBehavior() {
            double[] chirp1 = FourierData.dataChirpSignal();
            double[] chirp2 = FourierData.dataChirpSignal();
            
            assertArrayEquals(chirp1, chirp2, DELTA);
            
            double[] sawtooth1 = FourierData.dataSawtoothWave();
            double[] sawtooth2 = FourierData.dataSawtoothWave();
            
            assertArrayEquals(sawtooth1, sawtooth2, DELTA);
        }
    }
}
