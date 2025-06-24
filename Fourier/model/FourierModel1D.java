package Fourier.model;

import java.awt.Point;

import Fourier.Complex;
import Fourier.view.FourierView1D;

public class FourierModel1D extends FourierModel  {

	private FourierView1D fourierView1D;

    private Complex[] complexOriginData;
    private double[] calculatedData;

    public double[] getCalculatedData() {
        return calculatedData; 
    }

    public Complex[] getComplexResult() {
    return this.complexOriginData;
    }

    public void setComplexOriginData(Complex[] origin) {
        this.complexOriginData = origin.clone();  // cloneでコピーを作ってから返す
    }

    public void setOriginDataTransDoubltToComplex(double[] originData) {
        this.complexOriginData = new Complex[originData.length];
        Integer i = 0;
        for(double d : originData) {
            this.complexOriginData[i] = new Complex(d, 0);
            i += 1;
        }
    }

    public void computeFromMousePoint(Point point, Boolean isAltDown) {

    }

    public void setCalculatedData(double[] originData) {
        setOriginDataTransDoubltToComplex(originData);
        double[] oldCalculatedData = this.calculatedData;
        double[] newCalculatedData = new double[originData.length];
        Integer N = this.complexOriginData.length;

        
        fft(0, N);
        bitReverseReorder();

        Integer i = 0;
        for(Complex c : this.complexOriginData) {
            newCalculatedData[i] = c.magnitude() * c.magnitude();  // パワースペクトルを計算
            i += 1;
        }
        this.calculatedData = newCalculatedData;

        // Viewに変更した通知を送る
        firePropertyChange("1dData", oldCalculatedData, this.calculatedData);
    }

    // ビット反転インデックス
    public int bitReverse(int x, int bits) {
        int y = 0;
        for (int i = 0; i < bits; i++) {
            y = (y << 1) | (x & 1);
            x >>= 1;
        }
        return y;
    }

    // ビット反転で並び替え
    public void bitReverseReorder() {
        Integer N = this.complexOriginData.length;
            
        int bits = Integer.numberOfTrailingZeros(N);
        for (int i = 0; i < N; i++) {
            int j = bitReverse(i, bits);
            if (i < j) {
                Complex temp = this.complexOriginData[i];
                this.complexOriginData[i] = this.complexOriginData[j];
                this.complexOriginData[j] = temp;
            }
        }
    }

    // FFT
    public  void fft(int start, int n) {
        if (n == 1) return;
        int half = n / 2;
            

        for (int k = 0; k < half; k++) {
            int i = start + k;
            int j = i + half;

            double angle = -2 * Math.PI * k / n;
            Complex w = new Complex(Math.cos(angle), Math.sin(angle));
            Complex t = this.complexOriginData[j];
            Complex u = this.complexOriginData[i];
            this.complexOriginData[i] = u.add(t);
            this.complexOriginData[j] = w.mul(u.sub(t));
        }

        fft(start, half);
        fft(start + half, half);
            
    }

    // IFFT
    public  void ifft() {
        for (int i = 0; i < this.complexOriginData.length; i++) {
            this.complexOriginData[i] = this.complexOriginData[i].conjugate();
        }

        fft(0, this.complexOriginData.length);
        bitReverseReorder();

        for (int i = 0; i < this.complexOriginData.length; i++) {
            this.complexOriginData[i] = this.complexOriginData[i].conjugate().scale(1.0 / this.complexOriginData.length);
        }
    }
}
