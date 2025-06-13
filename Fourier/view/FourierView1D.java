package Fourier.view;

import Fourier.model.FourierModel1D;
import javax.swing.*;
import Fourier.FourierData;

public class FourierView1D extends FourierView {

    SignalWindow window;

    public FourierView1D(FourierModel1D model) {
        super(model);
    }

    private void createWindow(String title, double[] data) {
        this.window = new SignalWindow(title, data);
        this.window.setVisible(true);
    }

    @Override
    protected void updateView() {
    }
}
