package Fourier.example;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicInteger;
import Fourier.model.FourierModel1D;
import Fourier.model.FourierModel2D;
import Fourier.view.FourierView;
import Fourier.view.FourierView1D;
import Fourier.view.FourierView2D;
import Fourier.controller.FourierController1D;
import Fourier.controller.FourierController2D;
import Fourier.controller.MenuController;
import Fourier.FourierData;
import Fourier.FileIO;

/**
 * フーリエ変換デモアプリケーションのメインクラス。
 * 1次元と2次元のフーリエ変換デモウィンドウを起動します。
 */
public class Example {

    /** ウィンドウをずらす量 (ピクセル単位) */
    private static final int WINDOW_OFFSET = 30;
    /** 作成したウィンドウの数を数える静的カウンター */
    private static int windowCreationCount = 0;
    /** 現在開いているウィンドウの数を追跡するカウンター */
    private static final AtomicInteger openWindowCount = new AtomicInteger(0);

    /**
     * アプリケーションのエントリーポイント。
     * 1次元と2次元のフーリエ変換デモを起動します。
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1Dデモ - 全ての信号を表示
            restart1DDemoWithData(FourierData.dataSampleWave());
            restart1DDemoWithData(FourierData.dataChirpSignal());
            restart1DDemoWithData(FourierData.dataSawtoothWave());
            restart1DDemoWithData(FourierData.dataSquareWave());
            restart1DDemoWithData(FourierData.dataTriangleWave());

            // 初期データで2Dデモを開始
            double[][][] initialColorData = FileIO.readSignalFromImage("/JosephFourier2.jpg");
            if (initialColorData != null) {
                restart2DDemoWithData(initialColorData);
            } else {
                System.err.println("2D Demo Failed: Could not read initial image file.");
            }
        });
    }

    /**
     * 1Dデモのウィンドウを生成・表示するメソッド
     * @param signalData 表示する1次元信号データ
     */
    public static void restart1DDemoWithData(double[] signalData) {
        if (signalData == null) return;
        
        System.out.println("--- Starting/Restarting 1D Demo ---");
        FourierModel1D model1D = new FourierModel1D(signalData);
        FourierView1D view1D = new FourierView1D(model1D, windowCreationCount);
        windowCreationCount++;
        
        // ウィンドウ終了処理を追加
        addWindowCloseListener(view1D);
        
        // コントローラの登録

        // 1. スペクトル計算用コントローラを特定のパネルに登録
        FourierController1D controller1D = new FourierController1D(model1D);
        view1D.addMouseListenerToPanel("User Modified Power Spectrum", controller1D);
        view1D.addMouseMotionListenerToPanel("User Modified Power Spectrum", controller1D);

        // 2. メニュー表示用コントローラを生成し、全てのパネルに登録
        MenuController menuController1D = new MenuController(model1D);
        view1D.addMouseListenerToAllPanels(menuController1D);
        
        System.out.println("1D Demo Window Initialized.");
    }

    /**
     * 2Dデモのウィンドウを生成・表示するメソッド
     * @param colorImageData 表示する3次元(カラー)画像データ
     */
    public static void restart2DDemoWithData(double[][][] colorImageData) {
        if (colorImageData == null) return;

        System.out.println("\n--- Starting/Restarting 2D Demo ---");
        FourierModel2D model2D = new FourierModel2D(colorImageData);
        FourierView2D view2D = new FourierView2D(model2D, windowCreationCount);
        windowCreationCount++;
        
        // ウィンドウ終了処理を追加
        addWindowCloseListener(view2D);
        
        // コントローラの登録

        // 1. スペクトル計算用コントローラを特定のパネルに登録
        FourierController2D controller2D = new FourierController2D(model2D);
        view2D.addMouseListenerToPanel("User Modified Power Spectrum (Log Scale)", controller2D);
        view2D.addMouseMotionListenerToPanel("User Modified Power Spectrum (Log Scale)", controller2D);
        
        // 2. メニュー表示用コントローラを生成し、全てのパネルに登録
        MenuController menuController2D = new MenuController(model2D);
        view2D.addMouseListenerToAllPanels(menuController2D);
        
        System.out.println("2D Demo Window Initialized.");
    }
    
    /**
     * ウィンドウ終了時のリスナーを追加するヘルパーメソッド
     * @param view ウィンドウリスナーを追加するビュー
     */
    private static void addWindowCloseListener(FourierView view) {
        // ウィンドウカウンターを増加
        openWindowCount.incrementAndGet();
        
        // ウィンドウの位置をオフセットして設定
        int currentOffset = windowCreationCount * WINDOW_OFFSET;
        view.getFrame().setLocation(currentOffset, currentOffset);
        
        view.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // ウィンドウカウンターを減少
                int remainingWindows = openWindowCount.decrementAndGet();
                System.out.println("ウィンドウが閉じられました。残り: " + remainingWindows + " ウィンドウ");
                
                // 全てのウィンドウが閉じられた場合、アプリケーションを終了
                if (remainingWindows <= 0) {
                    System.out.println("全てのウィンドウが閉じられました。アプリケーションを終了します。");
                    System.exit(0);
                }
            }
        });
    }
}