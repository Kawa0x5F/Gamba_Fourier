package Fourier.example;

import javax.swing.SwingUtilities;
import Fourier.model.*;
import Fourier.view.*;
import Fourier.controller.*;
import Fourier.FourierData;
import Fourier.FileIO;

public class Example {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- 初期データで1Dデモを開始 ---
            restart1DDemoWithData(FourierData.dataSampleWave());

            // --- 初期データで2Dデモを開始 ---
            double[][][] initialColorData = FileIO.readSignalFromImage("/JosephFourier2.jpg");
            if (initialColorData != null) {
                restart2DDemoWithData(initialColorData);
            } else {
                System.err.println("2D Demo Failed: Could not read initial image file.");
            }
        });
    }

    /**
     * 1Dデモのウィンドウを生成・表示する public static メソッド
     * @param signalData 表示する1次元信号データ
     */
    public static void restart1DDemoWithData(double[] signalData) {
        if (signalData == null) return;
        
        System.out.println("--- Starting/Restarting 1D Demo ---");
        FourierModel1D model1D = new FourierModel1D(signalData);
        FourierView1D view1D = new FourierView1D(model1D);
        FourierController1D controller1D = new FourierController1D(model1D);

        // "User Modified Power Spectrum" パネルにリスナーを登録
        view1D.addMouseListenerToPanel("User Modified Power Spectrum", controller1D);
        view1D.addMouseMotionListenerToPanel("User Modified Power Spectrum", controller1D);
        System.out.println("1D Demo Window Initialized.");
    }

    /**
     * 2Dデモのウィンドウを生成・表示する public static メソッド
     * @param colorImageData 表示する3次元(カラー)画像データ
     */
    public static void restart2DDemoWithData(double[][][] colorImageData) {
        if (colorImageData == null) return;

        System.out.println("\n--- Starting/Restarting 2D Demo ---");
        // カラー画像をグレースケールに変換
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
        
        FourierModel2D model2D = new FourierModel2D(colorImageData);
        FourierView2D view2D = new FourierView2D(model2D);
        FourierController2D controller2D = new FourierController2D(model2D);
        
        // "User Modified Power Spectrum (Log Scale)" パネルにリスナーを登録
        view2D.addMouseListenerToPanel("User Modified Power Spectrum (Log Scale)", controller2D);
        view2D.addMouseMotionListenerToPanel("User Modified Power Spectrum (Log Scale)", controller2D);
        System.out.println("2D Demo Window Initialized.");
    }
}