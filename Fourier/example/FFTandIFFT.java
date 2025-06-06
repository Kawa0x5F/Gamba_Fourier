public class FFTandIFFT {

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

        public Complex conjugate() {
            return new Complex(this.re, -this.im);
        }

        public Complex scale(double alpha){
            return new Complex(this.re * alpha, this.im * alpha);
        }

        public String toString() {
            return String.format("%.3f%+.3fi", re, im);
        }
    }

    public static Complex[] fft(Complex[] x) {
        int N = x.length;
        
        if (N == 1) return new Complex[]{x[0]};

        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];
        for (int i = 0; i < (N / 2); i++) {
            even[i] = x[2 * i];
            odd[i] = x[2 * i + 1];
        }

        Complex[] Feven = fft(even);         // 現在のNに対する、各偶数行でのFFTの出力値を格納する配列Feven
        Complex[] Fodd = fft(odd);           // 現在のNに対する、各奇数行でのFFTの出力値を格納する配列Feven
        
        // バタフライ演算の実装
        Complex[] result = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double angle = -2 * Math.PI * k / N;                         // 単位円上のk番目の点(0<= k < N/2、kは整数)と原点を結んだ時の角度
            Complex wk = new Complex(Math.cos(angle), Math.sin(angle));  // angleに対する回転因子を算出

            // N点ごと(各行ごと)に求めたFFTを合成する
            Complex t = wk.mul(Fodd[k]);  // 奇数成分のDFT(FFT)に回転因子wkをかけることで、現在のN点分割したときの奇数行目を求める
            result[k] = Feven[k].add(t);  // 現在のN点分割したときの偶数行目(偶数成分のDFT)にtを足す
            result[k + N / 2] = Feven[k].sub(t); // 偶数行目(偶数成分のDFT)からtを引く
            // kとk+N/2の要素の結果を算出する理由は、入力信号時点でのN(今回の場合、N=8)をN=1となるまで分割した際、
            // 元々のN(入力時点でのN)点での入力の順番をビットリバースした時の順に合わせるため(バタフライ演算した時の出力のペア)
        }
        return result;
    }

    public static Complex[] ifft(Complex[] x) {
        int N = x.length;

        Complex[] x_conjugate = new Complex[N];
        for(int i = 0; i < N; i++){
            x_conjugate[i] = x[i].conjugate();
        }

        Complex[] y = fft(x_conjugate);
        
        Complex[] result = new Complex[N];
        for(int i = 0; i < N; i++){
            result[i] = y[i].conjugate().scale(1.0 / N);
        }
        return result;
    }

    public static void main(String[] args) {
        double[] input = {1, 0, 0, 1, 0, 0, 0, 1};
        Complex[] x = new Complex[input.length];
        for (int i = 0; i < input.length; i++) {
            x[i] = new Complex(input[i], 0);
        }
        
        // FFT
        Complex[] fft_result = fft(x);
        System.out.println("FFTの結果");
        for (Complex c : fft_result) {
            System.out.println(c);
        }

        System.out.println();

        // IFFT
        Complex[] ifft_result = ifft(fft_result);
        System.out.println("逆フーリエ変換(IFFT)の結果");
        for (Complex c : ifft_result) {
            System.out.println(c);
        }
    }
}

/* 参考：うさぎでもわかる信号処理・制御工学 第14羽高速フーリエ変換(FFT) https://www.momoyama-usagi.com/entry/math-seigyo14#4
        高速フーリエ変換 (FFTの逆変換含む)　https://ja.wikipedia.org/wiki/%E9%AB%98%E9%80%9F%E3%83%95%E3%83%BC%E3%83%AA%E3%82%A8%E5%A4%89%E6%8F%9B
 */
