package Fourier.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import Fourier.Complex;

/**
 * FourierModel1DクラスのFFTおよびIFFTの機能をテストするクラスです。
 * * @see FourierModel1D
 */
class FourierModel1DTest {

    private FourierModel1D fourierModel;
    // 浮動小数点数の比較に使用する許容誤差
    private static final double DELTA = 1e-9;

    /**
     * 各テストの実行前に、新しいFourierModel1Dインスタンスを初期化します。
     */
    @BeforeEach
    void setUp() {
        fourierModel = new FourierModel1D();
    }

    /**
     * FFT（高速フーリエ変換）の正確性をテストします。
     * <p>
     * N=4の既知の入力データに対してFFTを実行し、理論値と比較します。
     * 入力: [1, 2, 3, 4]
     * 理論的なFFT結果: [10, -2+2i, -2, -2-2i]
     * </p>
     * <p>
     * 注: このクラスのfftメソッドは、計算後にビット反転並べ替えが必要です。
     * そのため、fft()の後にbitReverseReorder()を呼び出して結果を検証します。
     * </p>
     */
    @Test
    @DisplayName("FFTが正しく計算されるかテスト")
    void testFFT() {
        // 1. セットアップ: テストデータ準備
        Complex[] inputData = {
            new Complex(1, 0),
            new Complex(2, 0),
            new Complex(3, 0),
            new Complex(4, 0)
        };
        fourierModel.setComplexOriginData(inputData);

        // 期待される結果 (理論値)
        Complex[] expectedOutput = {
            new Complex(10, 0),
            new Complex(-2, 2),
            new Complex(-2, 0),
            new Complex(-2, -2)
        };

        // 2. 実行: FFTを計算
        // この実装では、fft()の後にビット反転並べ替えが必要
        fourierModel.fft(0, inputData.length);
        fourierModel.bitReverseReorder();
        Complex[] actualOutput = fourierModel.getComplexResult();

        // 3. 検証: 結果が期待値と一致するか確認
        assertEquals(expectedOutput.length, actualOutput.length, "配列の長さが一致しません");
        for (int i = 0; i < expectedOutput.length; i++) {
            assertEquals(expectedOutput[i].getReal(), actualOutput[i].getReal(), DELTA, "Index " + i + " の実数部が一致しません");
            assertEquals(expectedOutput[i].getImaginary(), actualOutput[i].getImaginary(), DELTA, "Index " + i + " の虚数部が一致しません");
        }
    }

    /**
     * IFFT（逆高速フーリエ変換）の正確性をテストします。
     * <p>
     * あるデータに対してFFTを実行し、その結果にIFFTを適用すると、
     * 元のデータ（スケーリング誤差を除く）に戻ることを確認します。
     * IFFT(FFT(data)) == data
     * </p>
     */
    @Test
    @DisplayName("IFFTがFFTの結果を元のデータに戻せるかテスト")
    void testIFFT() {
        // 1. セットアップ: テストデータ準備
        Complex[] originalData = {
            new Complex(1, 0),
            new Complex(2, 0),
            new Complex(3, 0),
            new Complex(4, 0),
            new Complex(5, 0),
            new Complex(6, 0),
            new Complex(7, 0),
            new Complex(8, 0)
        };
        fourierModel.setComplexOriginData(originalData);

        // 2. 実行: FFT -> IFFT
        // まず順変換 (FFT)
        fourierModel.fft(0, originalData.length);
        fourierModel.bitReverseReorder();
        
        // 次に逆変換 (IFFT)
        fourierModel.ifft();
        Complex[] resultData = fourierModel.getComplexResult();

        // 3. 検証: 逆変換後のデータが元のデータと一致するか確認
        assertEquals(originalData.length, resultData.length, "配列の長さが一致しません");
        for (int i = 0; i < originalData.length; i++) {
            assertEquals(originalData[i].getReal(), resultData[i].getReal(), DELTA, "Index " + i + " の実数部が一致しません");
            assertEquals(originalData[i].getImaginary(), resultData[i].getImaginary(), DELTA, "Index " + i + " の虚数部が一致しません");
        }
    }
}

// 注意: このテストをコンパイル・実行するには、
// 以下の内容を想定したComplexクラスが必要です。

/*
package Fourier.model;

public class Complex {
    private final double real;
    private final double imag;

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public double getReal() { return real; }
    public double getImag() { return imag; }

    public Complex add(Complex b) {
        return new Complex(this.real + b.real, this.imag + b.imag);
    }
    
    public Complex sub(Complex b) {
        return new Complex(this.real - b.real, this.imag - b.imag);
    }

    public Complex mul(Complex b) {
        double real = this.real * b.real - this.imag * b.imag;
        double imag = this.real * b.imag + this.imag * b.real;
        return new Complex(real, imag);
    }

    public Complex conjugate() {
        return new Complex(this.real, -this.imag);
    }

    public Complex scale(double alpha) {
        return new Complex(this.real * alpha, this.imag * alpha);
    }
    
    public double magnitude() {
        return Math.sqrt(real * real + imag * imag);
    }
}
*/