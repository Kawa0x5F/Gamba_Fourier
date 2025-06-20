public class DFT {

    // 複素数クラス
    public static class Complex {
        public double re;
        public double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public Complex add(Complex b) {
            return new Complex(this.re + b.re, this.im + b.im);
        }

        public Complex mul(Complex b) {
            double real = this.re * b.re - this.im * b.im;
            double imag = this.re * b.im + this.im * b.re;
            return new Complex(real, imag);
        }

        @Override
        public String toString() {
            return String.format("%.3f%+.3fi", re, im);
        }
    }

    // DFTの実装（FFTを使わない）
    public static Complex[] dft(Complex[] x) {
        int N = x.length;
        Complex[] result = new Complex[N];

        for (int k = 0; k < N; k++) {
            Complex sum = new Complex(0, 0);
            for (int n = 0; n < N; n++) {
                double angle = -2 * Math.PI * k * n / N;
                Complex w = new Complex(Math.cos(angle), Math.sin(angle)); // e^{-j2πkn/N}
                sum = sum.add(x[n].mul(w));
            }
            result[k] = sum;
        }

        return result;
    }

    // メイン関数
    public static void main(String[] args) {
        double[] input = {1, 0, 0, 1, 0, 0, 0, 1};
        Complex[] x = new Complex[input.length];
        for (int i = 0; i < input.length; i++) {
            x[i] = new Complex(input[i], 0);
        }

        Complex[] result = dft(x);

        for (Complex c : result) {
            System.out.println(c);
        }
    }
}
