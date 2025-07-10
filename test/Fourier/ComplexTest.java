package Fourier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Complexクラスの単体テストクラス
 * 複素数の四則演算、絶対値、指数関数、極座標変換等のテストを行う
 * 
 * @author Generated Test
 * @see Complex
 */
class ComplexTest {

    private static final double DELTA = 1e-10;
    private Complex c1;
    private Complex c2;
    private Complex zero;
    private Complex one;
    private Complex i;

    @BeforeEach
    void setUp() {
        c1 = new Complex(3, 4);  // 3 + 4i
        c2 = new Complex(1, -2); // 1 - 2i
        zero = new Complex(0, 0);
        one = new Complex(1, 0);
        i = new Complex(0, 1);
    }

    @Nested
    @DisplayName("コンストラクタと基本メソッドのテスト")
    class ConstructorAndBasicMethodsTest {

        @Test
        @DisplayName("コンストラクタと取得メソッドが正常に動作する")
        void testConstructorAndGetters() {
            Complex c = new Complex(5.5, -3.2);
            assertEquals(5.5, c.getReal(), DELTA);
            assertEquals(-3.2, c.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("ゼロの複素数が正常に作成される")
        void testZeroComplex() {
            assertEquals(0.0, zero.getReal(), DELTA);
            assertEquals(0.0, zero.getImaginary(), DELTA);
        }
    }

    @Nested
    @DisplayName("四則演算のテスト")
    class ArithmeticOperationsTest {

        @Test
        @DisplayName("加算が正常に動作する")
        void testAddition() {
            Complex result = c1.add(c2); // (3+4i) + (1-2i) = 4+2i
            assertEquals(4.0, result.getReal(), DELTA);
            assertEquals(2.0, result.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("減算が正常に動作する")
        void testSubtraction() {
            Complex result = c1.sub(c2); // (3+4i) - (1-2i) = 2+6i
            assertEquals(2.0, result.getReal(), DELTA);
            assertEquals(6.0, result.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("乗算が正常に動作する")
        void testMultiplication() {
            Complex result = c1.mul(c2); // (3+4i) * (1-2i) = 11-2i
            assertEquals(11.0, result.getReal(), DELTA);
            assertEquals(-2.0, result.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("除算が正常に動作する")
        void testDivision() {
            Complex result = c1.div(c2); // (3+4i) / (1-2i) = -1+2i
            assertEquals(-1.0, result.getReal(), DELTA);
            assertEquals(2.0, result.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("ゼロで除算すると例外がスローされる")
        void testDivisionByZero() {
            assertThrows(ArithmeticException.class, () -> c1.div(zero));
        }

        @Test
        @DisplayName("虚数単位iの基本演算が正常に動作する")
        void testImaginaryUnitOperations() {
            Complex i2 = i.mul(i); // i * i = -1
            assertEquals(-1.0, i2.getReal(), DELTA);
            assertEquals(0.0, i2.getImaginary(), DELTA);
        }
    }

    @Nested
    @DisplayName("絶対値と位相のテスト")
    class MagnitudeAndPhaseTest {

        @Test
        @DisplayName("絶対値が正常に計算される")
        void testMagnitude() {
            double magnitude = c1.magnitude(); // |3+4i| = 5
            assertEquals(5.0, magnitude, DELTA);
        }

        @Test
        @DisplayName("ゼロの絶対値は0")
        void testZeroMagnitude() {
            assertEquals(0.0, zero.magnitude(), DELTA);
        }

        @Test
        @DisplayName("位相が正常に計算される")
        void testPhase() {
            double phase = one.getPhase(); // 1の位相は0
            assertEquals(0.0, phase, DELTA);
            
            double phaseI = i.getPhase(); // iの位相はπ/2
            assertEquals(Math.PI / 2, phaseI, DELTA);
        }

        @Test
        @DisplayName("極座標からの変換が正常に動作する")
        void testFromPolar() {
            Complex c = Complex.fromPolar(5.0, Math.PI / 4); // magnitude=5, phase=π/4
            assertEquals(5.0 * Math.cos(Math.PI / 4), c.getReal(), DELTA);
            assertEquals(5.0 * Math.sin(Math.PI / 4), c.getImaginary(), DELTA);
        }
    }

    @Nested
    @DisplayName("指数関数のテスト")
    class ExponentialFunctionTest {

        @Test
        @DisplayName("e^0 = 1")
        void testExpZero() {
            Complex result = Complex.exp(zero);
            assertEquals(1.0, result.getReal(), DELTA);
            assertEquals(0.0, result.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("e^(iπ) = -1 (オイラーの公式)")
        void testEulerFormula() {
            Complex iPi = new Complex(0, Math.PI);
            Complex result = Complex.exp(iPi);
            assertEquals(-1.0, result.getReal(), DELTA);
            assertEquals(0.0, result.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("e^(i*π/2) = i")
        void testExpImaginaryHalfPi() {
            Complex iPiHalf = new Complex(0, Math.PI / 2);
            Complex result = Complex.exp(iPiHalf);
            assertEquals(0.0, result.getReal(), DELTA);
            assertEquals(1.0, result.getImaginary(), DELTA);
        }
    }

    @Nested
    @DisplayName("共役と拡大縮小のテスト")
    class ConjugateAndScaleTest {

        @Test
        @DisplayName("共役複素数が正常に計算される")
        void testConjugate() {
            Complex conjugate = c1.conjugate(); // (3+4i)* = 3-4i
            assertEquals(3.0, conjugate.getReal(), DELTA);
            assertEquals(-4.0, conjugate.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("スカラー倍が正常に動作する")
        void testScale() {
            Complex scaled = c1.scale(2.0); // 2 * (3+4i) = 6+8i
            assertEquals(6.0, scaled.getReal(), DELTA);
            assertEquals(8.0, scaled.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("負のスカラー倍が正常に動作する")
        void testNegativeScale() {
            Complex scaled = c1.scale(-1.0); // -1 * (3+4i) = -3-4i
            assertEquals(-3.0, scaled.getReal(), DELTA);
            assertEquals(-4.0, scaled.getImaginary(), DELTA);
        }
    }

    @Nested
    @DisplayName("文字列表現のテスト")
    class ToStringTest {

        @Test
        @DisplayName("実数のみの場合の文字列表現")
        void testRealOnlyToString() {
            Complex real = new Complex(5.0, 0.0);
            assertEquals("5.0", real.toString());
        }

        @Test
        @DisplayName("虚数のみの場合の文字列表現")
        void testImaginaryOnlyToString() {
            Complex imag = new Complex(0.0, 3.0);
            assertEquals("3.0i", imag.toString());
        }

        @Test
        @DisplayName("正の虚部を持つ複素数の文字列表現")
        void testPositiveImaginaryToString() {
            Complex c = new Complex(2.0, 3.0);
            assertEquals("2.0 + 3.0i", c.toString());
        }

        @Test
        @DisplayName("負の虚部を持つ複素数の文字列表現")
        void testNegativeImaginaryToString() {
            Complex c = new Complex(2.0, -3.0);
            assertEquals("2.0 - 3.0i", c.toString());
        }

        @Test
        @DisplayName("ゼロの文字列表現")
        void testZeroToString() {
            assertEquals("0.0", zero.toString());
        }
    }

    @Nested
    @DisplayName("数学的性質のテスト")
    class MathematicalPropertiesTest {

        @Test
        @DisplayName("加算の交換法則: a + b = b + a")
        void testAdditionCommutative() {
            Complex ab = c1.add(c2);
            Complex ba = c2.add(c1);
            assertEquals(ab.getReal(), ba.getReal(), DELTA);
            assertEquals(ab.getImaginary(), ba.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("乗算の交換法則: a * b = b * a")
        void testMultiplicationCommutative() {
            Complex ab = c1.mul(c2);
            Complex ba = c2.mul(c1);
            assertEquals(ab.getReal(), ba.getReal(), DELTA);
            assertEquals(ab.getImaginary(), ba.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("乗算の単位元: a * 1 = a")
        void testMultiplicationIdentity() {
            Complex result = c1.mul(one);
            assertEquals(c1.getReal(), result.getReal(), DELTA);
            assertEquals(c1.getImaginary(), result.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("加算の単位元: a + 0 = a")
        void testAdditionIdentity() {
            Complex result = c1.add(zero);
            assertEquals(c1.getReal(), result.getReal(), DELTA);
            assertEquals(c1.getImaginary(), result.getImaginary(), DELTA);
        }

        @Test
        @DisplayName("共役の性質: |z|² = z * z*")
        void testConjugateProperty() {
            Complex conjugate = c1.conjugate();
            Complex product = c1.mul(conjugate);
            double magnitudeSquared = c1.magnitude() * c1.magnitude();
            
            assertEquals(magnitudeSquared, product.getReal(), DELTA);
            assertEquals(0.0, product.getImaginary(), DELTA);
        }
    }
}
