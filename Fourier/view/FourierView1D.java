package Fourier.view;

import Fourier.model.FourierModel1D;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

public class FourierView1D extends FourierView implements PropertyChangeListener {

    private static final String KEY_ORIGINAL = "Original Signal";
    private static final String KEY_SPECTRUM_INITIAL = "Original Power Spectrum";
    private static final String KEY_IFFT_RESULT = "Reconstructed Signal";
    private static final String KEY_USER_MOD_SPECTRUM = "User Modified Power Spectrum";

    public static final int PANEL_WIDTH = 400;
    public static final int PANEL_HEIGHT = 250;

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
        // InfoSignalPanelは内部クラスで直接修正
        addPanel(KEY_USER_MOD_SPECTRUM, new InfoSignalPanel(KEY_USER_MOD_SPECTRUM));

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

        String propertyName = evt.getPropertyName();
        if (propertyName.equals("calculationPoint") || propertyName.equals("altKeyState")) {
            FourierModel1D model1D = (FourierModel1D) getModel();
            if (panels.get(KEY_USER_MOD_SPECTRUM) instanceof InfoSignalPanel) {
                ((InfoSignalPanel) panels.get(KEY_USER_MOD_SPECTRUM)).setCalculationInfo(model1D.getLastCalculationPoint(), model1D.getIsAltDown());
            }
        }
    }

    private class InfoSignalPanel extends SignalPanel {
        private Point calculationPoint;
        private boolean altPressed;

        public InfoSignalPanel(String title) {
            super(title);
            // --- InfoSignalPanelにも推奨サイズを設定 ---
            this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        }

        public void setCalculationInfo(Point point, boolean alt) {
            this.calculationPoint = point;
            this.altPressed = alt;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (calculationPoint != null) {
                g.setColor(Color.RED);
                String info = String.format("Last Click Point: (%d, %d)", calculationPoint.x, calculationPoint.y);
                g.drawString(info, 10, getHeight() - 40);

                String altInfo = String.format("Alt Key Pressed: %b", altPressed);
                g.drawString(altInfo, 10, getHeight() - 20);
            }
        }
    }
}