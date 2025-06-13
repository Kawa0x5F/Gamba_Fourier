package Fourier;

/**
 * 複素数を表現するクラス
 * 四則演算・絶対値計算・指数関数$e^c$の実装を提供する
 * 
 * @author Yuichi Kawasaki
 */
public class Complex {
    private final double real; // 実部
    private final double imag; // 虚部

    /**
     * 引数の値から実部と虚部から新しい {@code Complex} オブジェクトを生成する
     *
     * @param real この複素数の実部
     * @param imag この複素数の虚部
     */
    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * この複素数の実部の値を返すメソッド
     * @return この複素数の実部
     */
    public double getReal() {
        return real;
    }

    /**
     * この複素数の虚部の値を返すメソッド
     * @return この複素数の虚部
     */
    public double getImaginary() {
        return imag;
    }

    /**
     * この複素数に指定された複素数 {@code c} を加算した結果を、新しい {@code Complex} オブジェクトとして返す
     * @param c　この複素数に加算する複素数
     * @return 加算結果の新規 {@code Complex} オブジェクト
     */
    public Complex add(Complex c) {
        return new Complex(real + c.real, imag + c.imag);
    }

    /**
     * この複素数に指定された複素数 {@code c} を減算した結果を、新しい {@code Complex} オブジェクトとして返す
     * @param c　この複素数に減算する複素数
     * @return 減算結果の新規 {@code Complex} オブジェクト
     */
    public Complex sub(Complex c) {
        return new Complex(real - c.real, imag - c.imag);
    }

    /**
     * この複素数に指定された複素数 {@code c} を乗算した結果を、新しい {@code Complex} オブジェクトとして返す
     * @param c　この複素数に乗算する複素数
     * @return 乗算結果の新規 {@code Complex} オブジェクト
     */
    public Complex mul(Complex c) {
        return new Complex(
            real*c.real - imag*c.imag,
            real*c.imag + imag*c.real
        );
    }

    /**
     * この複素数に指定された複素数 {@code c} を除算した結果を、新しい {@code Complex} オブジェクトとして返す
     * @param c　この複素数に除算する複素数
     * @return 除算結果の新規 {@code Complex} オブジェクト
    　* @throws ArithmeticException {@code c} がゼロ (0+0i) の場合例外をスロー
     */
    public Complex div(Complex c) {
        double denominator = c.real*c.real + c.imag*c.imag;
        if (denominator == 0) {
            throw new ArithmeticException("Division by zero complex number");
        }

        return new Complex(
            (real*c.real + imag*c.imag) / denominator,
            (imag*c.real - real*c.imag) / denominator
        );
    }

    /**
     * この複素数の絶対値を返す
     * @return この複素数の絶対値
     */
    public double magnitude() {
        return Math.hypot(this.real, this.imag);
    }

    /**
     * 指定された複素数 {@code c} の指数関数 $e^c$ を計算し、新規 {@code Complex} オブジェクトとして返す
     * @param c 指数を計算する対象の複素数
     * @return $e^c$ の計算結果を表す新規 {@code Complex} オブジェクト
     */
    public static Complex exp(Complex c) {
        double expReal = Math.exp(c.real);
        return new Complex(
            expReal * Math.cos(c.imag),
            expReal * Math.sin(c.imag)
        );
    }

    /**
     * この複素数の実数と虚数を反転させた結果を、新しい {@code Complex} オブジェクトとして返す
     * @return 富豪を反転させた新規{@code Complex} オブジェクト
     */
    public Complex conjugate() {
        return new Complex(this.real, -this.imag);
    }

    /**
     * この複素数をスカラー値で乗算します。
     *
     * @param alpha 乗算するスカラー値（実数）
     * @return      この複素数を {@code alpha} 倍した新しい {@code Complex} オブジェクト
     */
    public Complex scale(double alpha){
        return new Complex(this.real * alpha, this.imag * alpha);
    }

    /**
     * この複素数の文字列表現を返す
     * @return この複素数を文字列で表現した文字列
     */
    @Override
    public String toString() {
        if (imag == 0) {
            return String.valueOf(real);
        }
        if (real == 0) {
            return String.valueOf(imag) + "i";
        }
        // 虚部が負の場合は、符号を "-" にして絶対値を追加
        if (imag < 0) {
            return real + " - " + (-imag) + "i";
        }
        // 虚部が正の場合は、"+" でつなぐ
        return real + " + " + imag + "i";
    }
}