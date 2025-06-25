package Fourier.view;

import Fourier.Complex;
import Fourier.model.MockFourierModel1D;
import Fourier.view.FourierView1D;
import Fourier.view.SignalPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FourierView1DTest {

    private MockFourierModel1D mockModel;
    private FourierView1D view;
    private CountDownLatch latch; // 非同期処理の完了を待つためのラッチ

    // 各テストが実行される前に呼ばれるセットアップメソッド
    @BeforeEach
    void setUp() throws Exception {
        // Swingコンポーネントの操作はEvent Dispatch Thread (EDT) で行うのが安全
        SwingUtilities.invokeAndWait(() -> {
            mockModel = new MockFourierModel1D();
            
            // updateViewが呼ばれたことをテスト側で検知するために、
            // FourierView1Dを匿名クラスで拡張し、updateViewをオーバーライドする
            view = new FourierView1D(mockModel) {
                @Override
                protected void updateView() {
                    super.updateView();
                    if (latch != null) {
                        latch.countDown(); // updateViewが呼ばれたらラッチを解放
                    }
                }
            };
            // テスト実行中にウィンドウが実際に表示されないようにする
            view.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            view.setVisible(false);
        });
    }

    /**
     * FourierView1Dのコンストラクタが正しくコンポーネントを初期化するかをテスト
     */
    @Test
    void testInitialization() {
        // フレームのタイトルを検証
        assertEquals("1D Fourier Transform", view.frame.getTitle());

        // レイアウトマネージャを検証
        assertTrue(view.frame.getContentPane().getLayout() instanceof GridLayout);
        GridLayout layout = (GridLayout) view.frame.getContentPane().getLayout();
        assertEquals(2, layout.getRows());
        assertEquals(2, layout.getColumns());

        // 4つのパネルが登録されているかを検証
        assertEquals(4, view.panels.size());
        assertTrue(view.panels.containsKey("Original Signal"));
        assertTrue(view.panels.containsKey("Power Spectrum"));
        assertTrue(view.panels.get("Original Signal") instanceof SignalPanel);
    }

    /**
     * モデルのプロパティ変更イベントにより、ビューが非同期で更新されることをテスト
     */
    @Test
    void testUpdateOnPropertyChange() throws InterruptedException {
        // 1. テスト用のダミー結果データを作成
        double[] dummySpectrum = new double[]{16.0, 4.0, 0.0};
        Complex[] dummyComplex = new Complex[]{new Complex(4, 0), new Complex(2, 0), new Complex(0, 0)};
        
        // 2. モックにダミーデータを設定
        mockModel.setDummyResults(dummySpectrum, dummyComplex);

        // ラッチを初期化
        latch = new CountDownLatch(1);

        // 3. モデルの計算トリガーを呼び出す（引数の元データはここでは重要ではない）
        SwingUtilities.invokeLater(() -> {
            mockModel.setCalculatedData(new double[]{1, 2, 3});
        });

        // 4. updateViewが呼ばれるのを待つ
        assertTrue(latch.await(2, TimeUnit.SECONDS), "View update timed out.");

        // 5. ビューの各パネルがダミーデータで更新されたかを検証
        SignalPanel spectrumPanel = (SignalPanel) view.panels.get("Power Spectrum");
        assertArrayEquals(dummySpectrum, spectrumPanel.getData(), "Power Spectrum panel was not updated correctly.");

        // (必要であれば)実部・虚部パネルも同様に検証
        SignalPanel realPanel = (SignalPanel) view.panels.get("Fourier Transform (Real)");
        double[] expectedReal = new double[dummyComplex.length];
        for(int i=0; i<dummyComplex.length; i++) expectedReal[i] = dummyComplex[i].getReal();
        assertArrayEquals(expectedReal, realPanel.getData(), "Real part panel was not updated correctly.");
    }
}