package Fourier.example;

import javax.swing.SwingUtilities;

import Fourier.model.FourierModel1D;
import Fourier.model.FourierModel2D;
import Fourier.view.FourierView1D;
import Fourier.view.FourierView2D;
import Fourier.FourierData;
import Fourier.FileIO;
import Fourier.controller.FourierController1D;
import Fourier.controller.FourierController2D;

public class Example {

    @SuppressWarnings("unused")
    private static FourierView1D view1D;
    @SuppressWarnings("unused")
    private static FourierController1D controller1D; 
    
    @SuppressWarnings("unused")
    private static FourierView2D view2D;
    @SuppressWarnings("unused")
    private static FourierController2D controller2D;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- 1次元のデモ ---
            System.out.println("--- Starting 1D Demo ---");
            double[] signalData = FourierData.dataSampleWave(); 
            FourierModel1D model1D = new FourierModel1D(signalData); 
            view1D = new FourierView1D(model1D);
            controller1D = new FourierController1D(model1D); 
            
            // *** 変更点 ***
            // 修正された親クラスのメソッドを使って、正しいキーでリスナーを登録
            view1D.addMouseListenerToPanel("User Modified Power Spectrum", controller1D);
            view1D.addMouseMotionListenerToPanel("User Modified Power Spectrum", controller1D);

            System.out.println("1D Demo Window Initialized.");


            // --- 2次元のデモ ---
            System.out.println("\n--- Starting 2D Demo ---");
            String imagePath = "/JosephFourier2.jpg";
            
            double[][][] colorImageData = FileIO.readSignalFromImage(imagePath);

            if (colorImageData != null) {
                int height = colorImageData[0].length;
                int width = colorImageData.length;
                double[][] grayScaleData = new double[height][width];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        double r = colorImageData[x][y][0];
                        double g = colorImageData[x][y][1];
                        double b = colorImageData[x][y][2];
                        grayScaleData[y][x] = 0.299 * r + 0.587 * g + 0.114 * b;
                    }
                }
                
                FourierModel2D model2D = new FourierModel2D(grayScaleData);
                view2D = new FourierView2D(model2D);
                controller2D = new FourierController2D(model2D);
                
                // *** 変更点 ***
                // 2次元でも同様に、正しいキーでリスナーを登録
                view2D.addMouseListenerToPanel("User Modified Power Spectrum (Log Scale)", controller2D);
                view2D.addMouseMotionListenerToPanel("User Modified Power Spectrum (Log Scale)", controller2D);

                System.out.println("2D Demo Window Initialized.");

            } else {
                System.err.println("2D Demo Failed: Could not read image file from path: " + imagePath);
            }
        });
    }
}