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

    //static double[] input = GenerateBinaryData.CreateBinaryData(); 
        double[] input = {1, 0, 0, 1, 0, 0, 0, 1};
        Complex[] data = complexOriginData;
        int N = input.length;

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
        fft(0, N);
        Integer i = 0;
        for(Complex c : data) {
            newCalculatedData[i] = c.magnitude();
            i += 1;
        }
        this.calculatedData = newCalculatedData;

        // Viewに変更した通知を送る
        firePropertyChange("1dData", oldCalculatedData, this.calculatedData);
    }

   
        

        // ビット反転インデックス
        static int bitReverse(int x, int bits) {
            int y = 0;
            for (int i = 0; i < bits; i++) {
                y = (y << 1) | (x & 1);
                x >>= 1;
            }
            return y;
        }

        // ビット反転で並び替え（FFTの前処理）
        public  void bitReverseReorder() {
            int bits = Integer.numberOfTrailingZeros(N);
            for (int i = 0; i < N; i++) {
                int j = bitReverse(i, bits);
                if (i < j) {
                    Complex temp = data[i];
                    data[i] = data[j];
                    data[j] = temp;
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
                Complex t = data[j];
                Complex u = data[i];
                data[i] = u.add(t);
                data[j] = w.mul(u.sub(t));
            }

            fft(start, half);
            fft(start + half, half);
            
        }

        // IFFT
        public  void ifft() {
            for (int i = 0; i < N; i++) {
                data[i] = data[i].conjugate();
            }

            fft(0, N);

            for (int i = 0; i < N; i++) {
                data[i] = data[i].conjugate().scale(1.0 / N);
            }
        }

    //     public static void FFTandIFFT_test() {
    //         // 入力初期化
    //         for (int i = 0; i < N; i++) {
    //             data[i] = new Complex(input[i], 0);
    //         }

    //         // FFT（インプレース）
    //         fft(0, N);

    //         // ビット反転順に並び替え
    //         bitReverseReorder();

    //         System.out.println("=== FFT Result ===");
    //         for (Complex c : data) {
    //             System.out.println(c);
    //         }

    //         // IFFT
    //         ifft();

    //         // ビット反転順に並び替え
    //         bitReverseReorder();

    //         System.out.println("\n=== IFFT Result ===");
    //         for (Complex c : data) {
    //             System.out.println(c);
    //         }
    //     }
}
