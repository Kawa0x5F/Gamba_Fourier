package Fourier.model;

import Fourier.Complex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FourierModel1DTest {

    // 浮動小数点数の計算誤差を許容するための「デルタ（許容誤差）」
    private static final double DELTA = 1e-9;

    /**
     * fftメソッドの基本的な動作を検証するテスト。
     * 既知の入力に対して、期待される出力が得られるかを確認します。
     * 入力: [1, 0, 0, 0] -> 出力: [1, 1, 1, 1]
     */
    @Test
    @DisplayName("fft: [1,0,0,0] のような単純な入力で正しく変換されるか")
    void testFft_SimpleInput() {
        // 1. 準備 (Arrange)
        Complex[] input = {
            new Complex(1.0, 0.0),
            new Complex(0.0, 0.0),
            new Complex(0.0, 0.0),
            new Complex(0.0, 0.0)
        };

        Complex[] expected = {
            new Complex(1.0, 0.0),
            new Complex(1.0, 0.0),
            new Complex(1.0, 0.0),
            new Complex(1.0, 0.0)
        };

        // 2. 実行 (Act)
        Complex[] actual = FourierModel1D.fft(input);

        // 3. 検証 (Assert)
        assertNotNull(actual, "fftの結果がnullであってはならない");
        assertEquals(expected.length, actual.length, "結果の配列長が期待値と異なる");
        assertComplexArrayEquals(expected, actual, "FFTの計算結果が期待値と異なる");
    }

    /**
     * fftとifftが互いに逆の変換であることを検証する「ラウンドトリップ」テスト。
     * あるデータにfftを適用し、その結果にifftを適用すると、元のデータに復元されるはずです。
     */
    @Test
    @DisplayName("ifft(fft(x)) が元の値 x に復元されるか (ラウンドトリップテスト)")
    void testIfft_RoundTrip() {
        // 1. 準備 (Arrange)
        Complex[] originalData = {
            new Complex(0.5, 0.0),
            new Complex(1.2, 0.0),
            new Complex(3.4, 0.0),
            new Complex(0.9, 0.0)
        };

        // 2. 実行 (Act)
        Complex[] transformedData = FourierModel1D.fft(originalData);
        Complex[] restoredData = FourierModel1D.ifft(transformedData);

        // 3. 検証 (Assert)
        assertNotNull(restoredData, "ifftの結果がnullであってはならない");
        assertEquals(originalData.length, restoredData.length, "復元後の配列長が期待値と異なる");
        assertComplexArrayEquals(originalData, restoredData, "FFT -> IFFT の結果が元のデータと一致しない");
    }

    /**
     * `setOriginDataTransDoubltToComplex` メソッドを間接的にテストする。
     * このメソッドは private な `complexOriginData` を更新するため直接は検証しにくい。
     * ここでは `setCalculatedData` を通じて、その一部の動作を検証する。
     *
     * 注意: このテストは `fft` メソッドの実装に依存します。
     */
    @Test
    @DisplayName("setCalculatedData: double[] を渡した時に calculatedData が正しく計算されるか")
    void testSetCalculatedData() {
        // 1. 準備 (Arrange)
        FourierModel1D model = new FourierModel1D();
        double[] input = {1.0, 0.0, 0.0, 0.0};
        // fft([1,0,0,0]) -> [1,1,1,1] となり、各成分の絶対値(magnitude)は 1.0 になるはず
        double[] expectedMagnitudes = {1.0, 1.0, 1.0, 1.0};

        // 2. 実行 (Act)
        model.setCalculatedData(input);
        double[] actualMagnitudes = model.getCalculatedData();

        // 3. 検証 (Assert)
        assertNotNull(actualMagnitudes, "計算結果がnullであってはならない");
        assertEquals(expectedMagnitudes.length, actualMagnitudes.length, "計算結果の配列長が期待値と異なる");

        for (int i = 0; i < expectedMagnitudes.length; i++) {
            assertEquals(expectedMagnitudes[i], actualMagnitudes[i], DELTA,
                "calculatedData[" + i + "] の値が期待値と異なる");
        }
    }


    /**
     * Complex配列同士を比較するための補助メソッド。
     * 浮動小数点の誤差を考慮して実部と虚部をそれぞれ比較します。
     * @param expected 期待値の配列
     * @param actual 実際の値の配列
     * @param message 検証失敗時のメッセージ
     */
    private void assertComplexArrayEquals(Complex[] expected, Complex[] actual, String message) {
        for (int i = 0; i < expected.length; i++) {
            String elementMessage = message + " (要素 " + i + ")";
            assertEquals(expected[i].getReal(), actual[i].getReal(), DELTA, elementMessage + " の実部が不一致");
            assertEquals(expected[i].getImaginary(), actual[i].getImaginary(), DELTA, elementMessage + " の虚部が不一致");
        }
    }
}