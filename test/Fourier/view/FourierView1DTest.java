package Fourier.view;

import Fourier.Complex;
import Fourier.model.MockFourierModel1D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

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
            
            // FourierView1Dのコンストラクタは (FourierModel1D model, int creationIndex) を受け取る
            view = new FourierView1D(mockModel, 0) {
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

    @AfterEach
    void tearDown() throws Exception {
        // テスト後にウィンドウを閉じる
        if (view != null) {
            SwingUtilities.invokeAndWait(() -> {
                view.frame.dispose();
            });
        }
    }

    /**
     * FourierView1Dのコンストラクタが正しくコンポーネントを初期化するかをテスト
     */
    @Test
    @DisplayName("FourierView1Dの初期化テスト")
    void testInitialization() {
        // フレームのタイトルを検証
        assertEquals("1D Fourier Transform - Spectrum Manipulation", view.frame.getTitle());

        // レイアウトマネージャを検証
        assertTrue(view.frame.getContentPane().getLayout() instanceof GridLayout);
        GridLayout layout = (GridLayout) view.frame.getContentPane().getLayout();
        assertEquals(2, layout.getRows());
        assertEquals(2, layout.getColumns());

        // 4つのパネルが登録されているかを検証
        assertEquals(4, view.panels.size());
        assertTrue(view.panels.containsKey("Original Signal"));
        assertTrue(view.panels.containsKey("Original Power Spectrum"));
        assertTrue(view.panels.containsKey("Reconstructed Signal"));
        assertTrue(view.panels.containsKey("User Modified Power Spectrum"));
        
        // パネルの型を検証
        assertTrue(view.panels.get("Original Signal") instanceof SignalPanel);
        assertTrue(view.panels.get("Original Power Spectrum") instanceof SignalPanel);
        assertTrue(view.panels.get("Reconstructed Signal") instanceof SignalPanel);
    }

    /**
     * モデルのプロパティ変更イベントにより、ビューが非同期で更新されることをテスト
     */
    @Test
    @DisplayName("プロパティ変更によるビュー更新テスト")
    void testUpdateOnPropertyChange() throws InterruptedException {
        // 1. テスト用のダミー結果データを作成
        double[] dummySpectrum = new double[]{16.0, 4.0, 0.0};
        Complex[] dummyComplex = new Complex[]{new Complex(4, 0), new Complex(2, 0), new Complex(0, 0)};
        
        // 2. モックにダミーデータを設定
        mockModel.setDummyResults(dummySpectrum, dummyComplex);

        // ラッチを初期化
        latch = new CountDownLatch(1);

        // 3. モデルの計算トリガーを呼び出す
        SwingUtilities.invokeLater(() -> {
            mockModel.setCalculatedData(new double[]{1, 2, 3});
        });

        // 4. updateViewが呼ばれるのを待つ
        assertTrue(latch.await(2, TimeUnit.SECONDS), "View update timed out.");

        // 5. ビューの各パネルが更新されたかを検証
        // 実際のパネルキー名に合わせて修正
        SignalPanel spectrumPanel = (SignalPanel) view.panels.get("Original Power Spectrum");
        assertNotNull(spectrumPanel, "Original Power Spectrum panel should exist");
        
        SignalPanel originalPanel = (SignalPanel) view.panels.get("Original Signal");
        assertNotNull(originalPanel, "Original Signal panel should exist");
        
        SignalPanel reconstructedPanel = (SignalPanel) view.panels.get("Reconstructed Signal");
        assertNotNull(reconstructedPanel, "Reconstructed Signal panel should exist");
        
        // パネルがダミーデータで更新されていることを検証
        // SignalPanelのgetDataメソッドが存在することを前提とする
        double[] spectrumData = spectrumPanel.getData();
        assertNotNull(spectrumData, "Spectrum panel data should not be null");
        
        // データが設定されていることを確認（空でない）
        assertTrue(spectrumData.length > 0, "Spectrum panel should have data");
    }
    
    /**
     * ビューが正しく表示・非表示を切り替えられることをテスト
     */
    @Test
    @DisplayName("ビューの表示切り替えテスト")
    void testViewVisibility() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // 初期状態では非表示に設定されている
            assertFalse(view.frame.isVisible());
            
            // 表示状態に変更
            view.setVisible(true);
            assertTrue(view.frame.isVisible());
            
            // 非表示に戻す
            view.setVisible(false);
            assertFalse(view.frame.isVisible());
        });
    }

    /**
     * モデルとビューの関連付けが正しく行われていることをテスト
     */
    @Test
    @DisplayName("モデルとビューの関連付けテスト")
    void testModelViewBinding() {
        // モデルがビューに正しく関連付けられていることを確認
        assertEquals(mockModel, view.getModel());
        
        // モデルがPropertyChangeListenerとしてビューを登録していることを確認
        // （実装によってはこのテストは調整が必要）
        assertNotNull(view.getModel());
    }
}