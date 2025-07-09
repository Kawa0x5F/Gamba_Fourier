package Fourier;

/**
 * 複素数を表現するクラス
 * 四則演算・絶対値計算・指数関数$e^c$の実装を提供する
 * [高速化] FFT計算中のオブジェクト生成を避けるため、ミュータブル(可変)に変更されています。
 * * @author Yuichi Kawasaki
 */
public class Complex {
    // [高速化] finalを外し、ミュータブル(可変)に変更
    private double real; // 実部
    private double imag; // 虚部

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    // --- ゲッター/セッター ---
    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imag;
    }

    // [高速化] 自身の値を設定するセッター
    public void set(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    // [高速化] 他のComplexオブジェクトの値を自身にコピーする
    public void set(Complex c) {
        this.real = c.real;
        this.imag = c.imag;
    }

    // --- イミュータブルな（新しいオブジェクトを返す）四則演算 ---
    public Complex add(Complex c) {
        return new Complex(real + c.real, imag + c.imag);
    }
    public Complex sub(Complex c) {
        return new Complex(real - c.real, imag - c.imag);
    }
    public Complex mul(Complex c) {
        return new Complex(
            real*c.real - imag*c.imag,
            real*c.imag + imag*c.real
        );
    }
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

    // [高速化] 計算結果を自身に格納するミュータブルな乗算メソッド
    public void mulInPlace(Complex c) {
        double newReal = this.real * c.real - this.imag * c.imag;
        double newImag = this.real * c.imag + this.imag * c.real;
        this.real = newReal;
        this.imag = newImag;
    }

    public double magnitude() {
        return Math.hypot(this.real, this.imag);
    }

    public static Complex exp(Complex c) {
        double expReal = Math.exp(c.real);
        return new Complex(
            expReal * Math.cos(c.imag),
            expReal * Math.sin(c.imag)
        );
    }

    public Complex conjugate() {
        return new Complex(this.real, -this.imag);
    }

    public Complex scale(double alpha){
        return new Complex(this.real * alpha, this.imag * alpha);
    }

    // [高速化] 自身をスカラー倍するミュータブルなメソッド
    public void scaleInPlace(double alpha) {
        this.real *= alpha;
        this.imag *= alpha;
    }

    public double getPhase() {
        return Math.atan2(imag, real);
    }

    public static Complex fromPolar(double magnitude, double phase) {
        double realPart = magnitude * Math.cos(phase);
        double imagPart = magnitude * Math.sin(phase);
        return new Complex(realPart, imagPart);
    }

    @Override
    public String toString() {
        if (imag == 0) return String.valueOf(real);
        if (real == 0) return String.valueOf(imag) + "i";
        if (imag < 0) return real + " - " + (-imag) + "i";
        return real + " + " + imag + "i";
    }
}