package Fourier;

/**
 * 複素数を表現するクラス
 * 四則演算・絶対値計算・指数関数$e^c$の実装を提供する
 * FFT計算中のオブジェクト生成を避けるため、ミュータブル(可変)に変更されています。
 */
public class Complex {
    private double real; // 実部
    private double imag; // 虚部

    /**
     * 複素数を作成します。
     * @param real 実部
     * @param imag 虚部
     */
    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * 実部を取得します。
     * @return 実部
     */
    public double getReal() {
        return real;
    }

    /**
     * 虚部を取得します。
     * @return 虚部
     */
    public double getImaginary() {
        return imag;
    }

    /**
     * 自身の値を設定するセッター
     * @param real 実部
     * @param imag 虚部
     */
    public void set(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * 他のComplexオブジェクトの値を自身にコピーします。
     * @param c コピー元の複素数
     */
    public void set(Complex c) {
        this.real = c.real;
        this.imag = c.imag;
    }

    /**
     * 複素数の加算を行います（新しいオブジェクトを返す）。
     * @param c 加算する複素数
     * @return 加算結果の新しい複素数
     */
    public Complex add(Complex c) {
        return new Complex(real + c.real, imag + c.imag);
    }
    
    /**
     * 複素数の減算を行います（新しいオブジェクトを返す）。
     * @param c 減算する複素数
     * @return 減算結果の新しい複素数
     */
    public Complex sub(Complex c) {
        return new Complex(real - c.real, imag - c.imag);
    }
    
    /**
     * 複素数の乗算を行います（新しいオブジェクトを返す）。
     * @param c 乗算する複素数
     * @return 乗算結果の新しい複素数
     */
    public Complex mul(Complex c) {
        return new Complex(
            real*c.real - imag*c.imag,
            real*c.imag + imag*c.real
        );
    }
    
    /**
     * 複素数の除算を行います（新しいオブジェクトを返す）。
     * @param c 除算する複素数
     * @return 除算結果の新しい複素数
     * @throws ArithmeticException ゼロで割ろうとした場合
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
     * 計算結果を自身に格納するミュータブルな乗算メソッド
     * @param c 乗算する複素数
     */
    public void mulInPlace(Complex c) {
        double newReal = this.real * c.real - this.imag * c.imag;
        double newImag = this.real * c.imag + this.imag * c.real;
        this.real = newReal;
        this.imag = newImag;
    }

    /**
     * 複素数の絶対値（大きさ）を計算します。
     * @return 絶対値
     */
    public double magnitude() {
        return Math.hypot(this.real, this.imag);
    }

    /**
     * 複素数の指数関数を計算します。
     * @param c 指数部の複素数
     * @return exp(c)の計算結果
     */
    public static Complex exp(Complex c) {
        double expReal = Math.exp(c.real);
        return new Complex(
            expReal * Math.cos(c.imag),
            expReal * Math.sin(c.imag)
        );
    }

    /**
     * 複素数の共役を返します。
     * @return 共役複素数
     */
    public Complex conjugate() {
        return new Complex(this.real, -this.imag);
    }

    /**
     * 複素数をスカラー倍します（新しいオブジェクトを返す）。
     * @param alpha 倍率
     * @return スカラー倍した新しい複素数
     */
    public Complex scale(double alpha){
        return new Complex(this.real * alpha, this.imag * alpha);
    }

    /**
     * 自身をスカラー倍するミュータブルなメソッド
     * @param alpha 倍率
     */
    public void scaleInPlace(double alpha) {
        this.real *= alpha;
        this.imag *= alpha;
    }

    /**
     * 複素数の偏角（位相）を計算します。
     * @return 偏角（ラジアン）
     */
    public double getPhase() {
        return Math.atan2(imag, real);
    }

    /**
     * 極座標形式から複素数を作成します。
     * @param magnitude 大きさ
     * @param phase 位相（ラジアン）
     * @return 極座標から変換された複素数
     */
    public static Complex fromPolar(double magnitude, double phase) {
        double realPart = magnitude * Math.cos(phase);
        double imagPart = magnitude * Math.sin(phase);
        return new Complex(realPart, imagPart);
    }

    /**
     * 複素数の文字列表現を返します。
     * @return 複素数の文字列表現
     */
    @Override
    public String toString() {
        if (imag == 0) return String.valueOf(real);
        if (real == 0) return String.valueOf(imag) + "i";
        if (imag < 0) return real + " - " + (-imag) + "i";
        return real + " + " + imag + "i";
    }
}