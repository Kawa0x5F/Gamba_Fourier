package Fourier.model;

import java.awt.Point;

import Fourier.Complex;
import Fourier.view.FourierView2D;

public class FourierModel2D extends FourierModel {

    private Complex[][] complexOriginData;
    private double[][] calculatedData;

    public double[][] getCalculatedData() {
        return calculatedData;
    }

    public void setOriginDataTransDoubleToComplex(double[][] originData) {
        int line = originData.length;
        int column = originData[0].length;
        this.complexOriginData = new Complex[line][column];
        for (int i = 0; i < line; i++) {
            for (int j = 0; j < column; j++) {
                this.complexOriginData[i][j] = new Complex(originData[i][j], 0);
            }
        }
    }

    public void computeFromMousePoint(Point point, Boolean isAltDown) {

    }

    public void setCalculatedData(double[][] originData) {
        int line = originData.length;
        int column = originData[0].length;
        setOriginDataTransDoubleToComplex(originData);

        double[][] oldCalculatedData = this.calculatedData;
        double[][] newCalculatedData = new double[line][column];

        fft2D();

        for (int i = 0; i < line; i++) {
            for (int j = 0; j < column; j++) {
                newCalculatedData[i][j] = complexOriginData[i][j].magnitude() * complexOriginData[i][j].magnitude();
            }
        }

        this.calculatedData = newCalculatedData;
        firePropertyChange("2dData", oldCalculatedData, this.calculatedData);
    }

    // 2D FFT（行→転置→行→転置）
    public void fft2D() {
        int line = complexOriginData.length;
        int column = complexOriginData[0].length;
        FourierModel1D fft1D = new FourierModel1D();
        // 行方向
        for (int i = 0; i < line; i++) {
            Complex[] row = complexOriginData[i];
            double[] rowReal = new double[column];
            for (int j = 0; j < column; j++) {
                rowReal[j] = row[j].getReal();
            }

            fft1D.setCalculatedData(rowReal);

            Complex[] ffted = fft1D.getComplexResult();
            for (int j = 0; j < column; j++) {
                complexOriginData[i][j] = ffted[j];
            }
        }

        // 転置
        complexOriginData = transpose(complexOriginData);

            // 列方向（＝転置後の行）
        for (int i = 0; i < column; i++) {
            Complex[] row = complexOriginData[i];

            fft1D.setComplexOriginData(row);     // 複素数をそのまま渡す
            fft1D.fft(0, row.length);      // FFT実行
            fft1D.bitReverseReorder();           // 並び替え

            Complex[] ffted = fft1D.getComplexResult();
            for (int j = 0; j < line; j++) {
                complexOriginData[i][j] = ffted[j];
            }
        }


        // 再転置で元に戻す
        complexOriginData = transpose(complexOriginData);
    }

    // 転置処理
    public static Complex[][] transpose(Complex[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Complex[][] transposed = new Complex[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    public void ifft2D() {
    int line = complexOriginData.length;
    int column = complexOriginData[0].length;
    FourierModel1D fft1D = new FourierModel1D();

    // 行方向 IFFT
    for (int i = 0; i < line; i++) {
        fft1D.setComplexOriginData(complexOriginData[i]);
        fft1D.ifft();
        Complex[] iffted = fft1D.getComplexResult();
        for (int j = 0; j < column; j++) {
            complexOriginData[i][j] = iffted[j];
        }
    }

    // 転置
    complexOriginData = transpose(complexOriginData);

    // 列方向（転置後の行）IFFT
    for (int i = 0; i < column; i++) {
        fft1D.setComplexOriginData(complexOriginData[i]);
        fft1D.ifft();
        Complex[] iffted = fft1D.getComplexResult();
        for (int j = 0; j < line; j++) {
            complexOriginData[i][j] = iffted[j];
        }
    }

    // 再転置して元に戻す
    complexOriginData = transpose(complexOriginData);
}

}
