package Fourier.view;
import javax.swing.*;
import Fourier.model.FourierModel2D;

public class FourierView2D {
    // public static void main(String[] args) {
    //     // カラー画像表示
    //     double[][][] colorMatrix = FourierData.dataFourierColor();
    //     BufferedImage colorImage = ImageUtility.convertYUVToImage(colorMatrix);
        
    //     // グレースケール画像表示
    //     double[][] luminanceMatrix = FourierData.dataFourierGrayScale();
    //     BufferedImage grayImage = ImageUtility.convertLuminanceMatrixToImage(luminanceMatrix);
        
    //     SwingUtilities.invokeLater(() -> {
    //         createWindow2D("Color Image", colorImage);
    //         createWindow2D("Grayscale Image", grayImage);
    //     });
    // }

    // private static void createWindow2D(String title, BufferedImage data) {
    //     ImageWindow window = new ImageWindow(title, data);
    //     window.setVisible(true);
    // }
}
