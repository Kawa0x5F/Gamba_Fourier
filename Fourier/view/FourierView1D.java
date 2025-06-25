package Fourier.view;

import Fourier.Complex;
import Fourier.model.FourierModel1D;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FourierView1D extends FourierView implements PropertyChangeListener {

    // 各パネルを識別するためのキーとタイトル
    private static final String KEY_ORIGINAL = "Original Signal"; // 元の信号（時間領域）
    private static final String KEY_SPECTRUM_INITIAL = "Original Power Spectrum"; // 元のパワースペクトル（周波数領域）
    private static final String KEY_IFFT_RESULT = "Reconstructed Signal"; // IFFT結果（時間領域）
    private static final String KEY_USER_MOD_SPECTRUM = "User Modified Power Spectrum"; // ユーザー操作スペクトルと情報

    public static final int PANEL_WIDTH = 400; // Modelでも参照されるためpublic static
    public static final int PANEL_HEIGHT = 250; // マウスY座標の計算に必要なので追加

    public FourierView1D(FourierModel1D model) {
        super(model, "1D Fourier Transform - Spectrum Manipulation");

        frame.setSize(800, 600); // 画面サイズを調整
        frame.setLayout(new GridLayout(2, 2, 5, 5)); // 2x2グリッド

        // パネルの追加とタイトル設定
        addPanel(KEY_ORIGINAL, new SignalPanel(KEY_ORIGINAL)); // 左上: 元の信号
        addPanel(KEY_SPECTRUM_INITIAL, new SignalPanel(KEY_SPECTRUM_INITIAL)); // 右上: 元のパワースペクトル
        addPanel(KEY_IFFT_RESULT, new SignalPanel(KEY_IFFT_RESULT)); // 左下: IFFT再構成信号
        addPanel(KEY_USER_MOD_SPECTRUM, new InfoSignalPanel(KEY_USER_MOD_SPECTRUM)); // 右下: ユーザー操作スペクトルと情報

        model.addPropertyChangeListener(this);
        updateView(); // 初回表示の更新
        setVisible(true);
    }

    @Override
    protected void updateView() {
        FourierModel1D model1D = (FourierModel1D) getModel();

        // 1. オリジナルデータ (initialOriginData) - 左上
        double[] initialOriginData = model1D.getInitialOriginData();
        ((SignalPanel) panels.get(KEY_ORIGINAL)).setData(initialOriginData);

        // 2. 最初にオリジナル信号から計算されたパワースペクトル - 右上
        double[] initialPowerSpectrum = model1D.getInitialCalculatedPowerSpectrumData();
        ((SignalPanel) panels.get(KEY_SPECTRUM_INITIAL)).setData(initialPowerSpectrum);

        // 3. IFFTで再構成された時間領域データ (ifftResultData) - 左下
        double[] ifftResultData = model1D.getIfftResultData();
        if (ifftResultData != null) {
            ((SignalPanel) panels.get(KEY_IFFT_RESULT)).setData(ifftResultData);
        } else {
             ((SignalPanel) panels.get(KEY_IFFT_RESULT)).setData(new double[0]); // データがない場合は空の配列を設定
        }

        // 4. ユーザー操作によって再計算されたパワースペクトル (recalculatedPowerSpectrumData) - 右下
        // InfoSignalPanelにパワースペクトルデータを設定
        double[] recalculatedPowerSpectrumData = model1D.getRecalculatedPowerSpectrumData();
        if (recalculatedPowerSpectrumData != null) {
            ((InfoSignalPanel) panels.get(KEY_USER_MOD_SPECTRUM)).setData(recalculatedPowerSpectrumData);
        } else {
             ((InfoSignalPanel) panels.get(KEY_USER_MOD_SPECTRUM)).setData(new double[0]); // データがない場合は空の配列を設定
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        // userModifiedSpectrumData の変更（これはパワースペクトルとIFFT結果の両方に影響）
        if (propertyName.equals("userModifiedSpectrumData")) {
            // パワースペクトルとIFFT結果の更新はModel側でトリガーされるので、ここではView全体を更新する
            updateView();
        } 
        // recalculatedPowerSpectrumData の変更
        else if (propertyName.equals("recalculatedPowerSpectrumData")) {
            updateView();
        } 
        // ifftResultData の変更
        else if (propertyName.equals("ifftResultData")) {
            updateView();
        } 
        // initialCalculatedPowerSpectrumData は一度だけ計算されるので、個別で対応しても良い
        else if (propertyName.equals("initialCalculatedPowerSpectrumData")) {
            updateView(); 
        } 
        // 計算情報パネルのみの更新
        else if (propertyName.equals("calculationPoint") || propertyName.equals("altKeyState")) {
            FourierModel1D model1D = (FourierModel1D) getModel();
            Point calcPoint = model1D.getLastCalculationPoint();
            boolean altDown = model1D.getIsAltDown();

            if (panels.get(KEY_USER_MOD_SPECTRUM) instanceof InfoSignalPanel) {
                ((InfoSignalPanel) panels.get(KEY_USER_MOD_SPECTRUM)).setCalculationInfo(calcPoint, altDown);
            }
        }
    }

    // 計算情報を表示するためのSignalPanelのサブクラス
    private class InfoSignalPanel extends SignalPanel {
        private Point calculationPoint;
        private boolean altPressed;

        public InfoSignalPanel(String title) {
            super(title);
        }

        public void setCalculationInfo(Point point, boolean alt) {
            this.calculationPoint = point;
            this.altPressed = alt;
            repaint(); // 情報を更新したら再描画
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // SignalPanelの描画処理を呼び出す
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