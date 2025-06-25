package Fourier.example;

import javax.swing.SwingUtilities;
import Fourier.model.FourierModel1D;
import Fourier.view.FourierView1D;
import Fourier.FourierData;

public class Example {

    @SuppressWarnings("unused")
    private static FourierView1D view;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            double[] signalData = FourierData.dataSampleWave();
            FourierModel1D model = new FourierModel1D();
            // 静的フィールドにインスタンスを代入して参照を保持する
            view = new FourierView1D(model);

            model.setCalculatedData(signalData);
        });
    }
}
