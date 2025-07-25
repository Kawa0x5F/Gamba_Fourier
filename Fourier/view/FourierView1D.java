package Fourier.view;

import Fourier.model.FourierModel1D;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;

/**
 * 1次元フーリエ変換の結果を表示するビュークラス。
 * オリジナル信号、パワースペクトル、再構成信号、ユーザー変更スペクトルの4つのパネルを表示します。
 */
public class FourierView1D extends FourierView {

    private static final String KEY_ORIGINAL = "Original Signal";
    private static final String KEY_SPECTRUM_INITIAL = "Original Power Spectrum";
    private static final String KEY_IFFT_RESULT = "Reconstructed Signal";
    private static final String KEY_USER_MOD_SPECTRUM = "User Modified Power Spectrum";

    public static final int PANEL_WIDTH = 350;
    public static final int PANEL_HEIGHT = 160;

    /**
     * 1次元フーリエ変換ビューを作成します。
     * @param model 1次元フーリエ変換モデル
     * @param creationIndex ウィンドウ作成インデックス
     */
    public FourierView1D(FourierModel1D model, int creationIndex) {
        super(model, "1D Fourier Transform - Spectrum Manipulation");

        // --- 各パネルに推奨サイズを設定 ---

        // 左上: 元の信号
        JPanel originalPanel = new SignalPanel(KEY_ORIGINAL);
        originalPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        addPanel(KEY_ORIGINAL, originalPanel);

        // 右上: 元のパワースペクトル
        JPanel spectrumInitialPanel = new SignalPanel(KEY_SPECTRUM_INITIAL);
        spectrumInitialPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        addPanel(KEY_SPECTRUM_INITIAL, spectrumInitialPanel);

        // 左下: IFFT再構成信号
        JPanel ifftResultPanel = new SignalPanel(KEY_IFFT_RESULT);
        ifftResultPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        addPanel(KEY_IFFT_RESULT, ifftResultPanel);
        
        // 右下: ユーザー操作スペクトルと情報
        // 1. 元のスペクトルの最大値を取得
        FourierModel1D model1D = (FourierModel1D) getModel();
        double initialSpectrumMax = 0;
        double[] initialSpectrum = model1D.getInitialCalculatedPowerSpectrumData(); //
        if (initialSpectrum != null) {
            for (double v : initialSpectrum) {
                if (v > initialSpectrumMax) {
                    initialSpectrumMax = v;
                }
            }
        }

        // 2. InfoSignalPanelを生成し、最大値を設定
        InfoSignalPanel userSpectrumPanel = new InfoSignalPanel(KEY_USER_MOD_SPECTRUM);
        userSpectrumPanel.setFixedMaxValue(initialSpectrumMax);
        addPanel(KEY_USER_MOD_SPECTRUM, userSpectrumPanel);

        updateView();

        int offset = creationIndex * 30;
        frame.setLocation(offset, offset);

        // コンポーネントの推奨サイズに基づいてフレームサイズを自動調整
        frame.pack();
        
        // pack()後に、そのサイズを最小サイズとして設定
        frame.setMinimumSize(frame.getSize());

        setVisible(true);
    }

    @Override
    protected void updateView() {
        FourierModel1D model1D = (FourierModel1D) getModel();

        ((SignalPanel) panels.get(KEY_ORIGINAL)).setData(model1D.getInitialOriginData());
        ((SignalPanel) panels.get(KEY_SPECTRUM_INITIAL)).setData(model1D.getInitialCalculatedPowerSpectrumData());
        
        double[] ifftResultData = model1D.getIfftResultData();
        ((SignalPanel) panels.get(KEY_IFFT_RESULT)).setData(ifftResultData != null ? ifftResultData : new double[0]);

        double[] recalculatedPowerSpectrumData = model1D.getRecalculatedPowerSpectrumData();
        ((InfoSignalPanel) panels.get(KEY_USER_MOD_SPECTRUM)).setData(recalculatedPowerSpectrumData != null ? recalculatedPowerSpectrumData : new double[0]);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt); 
        // デバッグ用のマウスクリック座標表示は削除済み
    }

    private class InfoSignalPanel extends SignalPanel {
        // デバッグ用のマウスクリック座標表示は削除済み

        public InfoSignalPanel(String title) {
            super(title);
            // --- InfoSignalPanelにも推奨サイズを設定 ---
            this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        }

        // paintComponentはベースクラスのSignalPanelの実装をそのまま使用
    }
}