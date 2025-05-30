package Fourier;

/**
 * 複素数型
 */
public class Complex {
    private final double real;
    private final double imag;

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imag;
    }

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
        return new Complex(
            (real*c.real + imag*c.imag) / denominator,
            (imag*c.real - real*c.imag) / denominator
        );
    }

    public double magnitude() {
        return Math.sqrt(this.real * this.real + this.imag * this.imag);
    }

    public static Complex exp(Complex c) {
        double expReal = Math.exp(c.real);
        return new Complex(
            expReal * Math.cos(c.imag),
            expReal * Math.sin(c.imag)
        );
    }
}