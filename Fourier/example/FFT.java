public class FFT {

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

        public Complex sub(Complex b) {
            return new Complex(this.re - b.re, this.im - b.im);
        }

        public Complex mul(Complex b) {
            double real = this.re * b.re - this.im * b.im;
            double imag = this.re * b.im + this.im * b.re;
            return new Complex(real, imag);
        }

        public String toString() {
            return String.format("%.3f%+.3fi", re, im);
        }
    }

    public static Complex[] fft(Complex[] x) {
        int N = x.length;
        
        if (N == 1) return x;

        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];
        for (int i = 0; i < N / 2; i++) {
            even[i] = x[2 * i];
            odd[i] = x[2 * i + 1];
        }

        Complex[] Feven = fft(even);
        Complex[] Fodd = fft(odd);

        Complex[] result = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double angle = -2 * Math.PI * k / N;
            Complex wk = new Complex(Math.cos(angle), Math.sin(angle));
            Complex t = wk.mul(Fodd[k]);
            result[k] = Feven[k].add(t);
            result[k + N / 2] = Feven[k].sub(t);
        }
        return result;
    }

    public static void main(String[] args) {
        double[] input = {1, 0, 0, 1, 0, 0, 0, 1};
        Complex[] x = new Complex[input.length];
        for (int i = 0; i < input.length; i++) {
            x[i] = new Complex(input[i], 0);
        }

        Complex[] result = fft(x);
        System.out.println("FFT結果:");
        for (Complex c : result) {
            System.out.println(c);
        }
    }
}
