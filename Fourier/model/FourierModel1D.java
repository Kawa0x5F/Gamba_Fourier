package Fourier.model;

import Fourier.Complex;
import Fourier.view.FourierView1D;

public class FourierModel1D extends FourierModel {

	private FourierView1D fourierView1D;

    private Complex[] complexOriginData;
    private double[] calculatedData;

    public double[] getCalculatedData() {
        return calculatedData; 
    }

    public void setOriginDataTransDoubltToComplex(double[] originData) {
        this.complexOriginData = new Complex[originData.length];
        Integer i = 0;
        for(double d : originData) {
            this.complexOriginData[i] = new Complex(d, 0);
            i += 1;
        }
    }

    public void setCalculatedData(double[] originData) {
        setOriginDataTransDoubltToComplex(originData);
        double[] oldCalculatedData = this.calculatedData;
        double[] newCalculatedData = new double[originData.length];
        Complex[] result = fft(this.complexOriginData);
        Integer i = 0;
        for(Complex c : result) {
            newCalculatedData[i] = c.magnitude();
            i += 1;
        }
        this.calculatedData = newCalculatedData;

        // Viewに変更した通知を送る
        firePropertyChange("1dData", oldCalculatedData, this.calculatedData);
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
}
