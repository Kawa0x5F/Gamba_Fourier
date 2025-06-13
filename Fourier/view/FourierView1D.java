package Fourier.view;

import Fourier.model.FourierModel1D;
import javax.swing.*;
import Fourier.FourierData;

public class FourierView1D {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createWindow("Chirp Signal", FourierData.dataChirpSignal());
            createWindow("Sawtooth Wave", FourierData.dataSawtoothWave());
            createWindow("Square Wave", FourierData.dataSquareWave());
            createWindow("Sample Wave", FourierData.dataSampleWave());
            createWindow("Triangle Wave", FourierData.dataTriangleWave());
            createWindow("WAV Signal", WavReader.readWavFile("example.wav"));
        });
    }

    private static void createWindow(String title, double[] data) {
        SignalWindow window = new SignalWindow(title, data);
        window.setVisible(true);
    }
}
