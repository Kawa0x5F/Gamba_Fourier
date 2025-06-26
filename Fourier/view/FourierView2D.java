package Fourier.view;

import Fourier.model.FourierModel2D;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 2Dフーリエ変換のビュークラス。
 * すべてのパネル表示を3チャンネルのカラー画像を基準に行うように修正されています。
 */
public class FourierView2D extends FourierView implements PropertyChangeListener {

    // 各パネルを識別するためのキー (変更なし)
    private static final String KEY_ORIGINAL_IMAGE = "Original Image";
    private static final String KEY_ORIGINAL_SPECTRUM = "Original Power Spectrum (Log Scale)";
    private static final String KEY_RECONSTRUCTED_IMAGE = "Reconstructed Image";
    private static final String KEY_MODIFIED_SPECTRUM = "User Modified Power Spectrum (Log Scale)";

    public static final int PANEL_WIDTH = 400; //
    public static final int PANEL_HEIGHT = 400; //

    public FourierView2D(FourierModel2D model) {
        super(model, "2D Fourier Transform - Spectrum Manipulation");
        frame.setSize(850, 850); //
        frame.setLayout(new GridLayout(2, 2, 5, 5)); //

        // パネルを生成し、フレームに追加
        addPanel(KEY_ORIGINAL_IMAGE, new ImagePanel(KEY_ORIGINAL_IMAGE));
        addPanel(KEY_RECONSTRUCTED_IMAGE, new ImagePanel(KEY_RECONSTRUCTED_IMAGE));
        addPanel(KEY_ORIGINAL_SPECTRUM, new ImagePanel(KEY_ORIGINAL_SPECTRUM));
        addPanel(KEY_MODIFIED_SPECTRUM, new InfoImagePanel(KEY_MODIFIED_SPECTRUM));

        model.addPropertyChangeListener(this); //
        updateView(); //
        setVisible(true); //
    }

    /**
     * モデルの変更を検知し、ビュー（各パネル）を更新します。
     */
    @Override
    protected void updateView() {
        FourierModel2D model2D = (FourierModel2D) getModel();

        // 1. 元の画像 (カラーデータを直接設定)
        ((ImagePanel) panels.get(KEY_ORIGINAL_IMAGE)).setData(model2D.getInitialOriginColorData());

        // 2. IFFTで再構成された画像 (カラーデータを直接設定)
        ((ImagePanel) panels.get(KEY_RECONSTRUCTED_IMAGE)).setData(model2D.getIfftResultColorData());

        // 3. ユーザー操作によって再計算されたパワースペクトル
        // (グレースケールデータをカラー形式に変換して設定)
        double[][] spectrumData = model2D.getRecalculatedPowerSpectrumData();
        double[][][] spectrumAsColor = convertGrayDataToColorData(spectrumData, true); // 対数スケールを適用
        ((ImagePanel) panels.get(KEY_MODIFIED_SPECTRUM)).setData(spectrumAsColor);
    }
    
    /**
     * 単一チャンネルのグレースケールデータを、3チャンネルのカラーデータに変換します。
     * スペクトル表示のために使用します。
     * @param grayData 変換元のグレースケールデータ (double[][])
     * @param useLogScale 対数スケールを適用するかどうか
     * @return 変換後のカラーデータ (double[][][])
     */
    private double[][][] convertGrayDataToColorData(double[][] grayData, boolean useLogScale) {
        if (grayData == null || grayData.length == 0) return null;

        int height = grayData.length;
        int width = grayData[0].length;
        double[][][] colorData = new double[width][height][3];

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        // データの最大値・最小値を探す
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = useLogScale ? Math.log1p(grayData[y][x]) : grayData[y][x];
                if (val > max) max = val;
                if (val < min) min = val;
            }
        }

        double range = max - min;
        if (range == 0) range = 1;

        // データを0-255の輝度値に正規化し、RGB全チャンネルに同じ値を設定
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = useLogScale ? Math.log1p(grayData[y][x]) : grayData[y][x];
                double normalizedValue = 255 * (val - min) / range;
                colorData[x][y][0] = normalizedValue; // R
                colorData[x][y][1] = normalizedValue; // G
                colorData[x][y][2] = normalizedValue; // B
            }
        }
        return colorData;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "userModifiedSpectrumData":
            case "recalculatedPowerSpectrumData":
            case "ifftResultData":
                updateView();
                break;
            
            case "calculationPoint":
            case "altKeyState":
                FourierModel2D model2D = (FourierModel2D) getModel();
                if (panels.get(KEY_MODIFIED_SPECTRUM) instanceof InfoImagePanel) {
                    ((InfoImagePanel) panels.get(KEY_MODIFIED_SPECTRUM)).setCalculationInfo(
                        model2D.getLastCalculationPoint(),
                        model2D.getIsAltDown()
                    );
                }
                break;
        }
    }

    /**
     * 3チャンネルのカラーデータを画像として表示するパネル。
     * グレースケール専用の処理は削除されています。
     */
    protected class ImagePanel extends SignalPanel {
        private BufferedImage image;

        public ImagePanel(String title) {
            super(title);
            this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        }

        /**
         * カラーデータを設定し、パネルを再描画します。
         * @param colorData [width][height][channel] のデータ (0:R, 1:G, 2:B)
         */
        public void setData(double[][][] colorData) {
            this.image = (colorData == null) ? null : convertToImage(colorData);
            repaint();
        }

        /**
         * カラーデータ(double[][][])をBufferedImageに変換します。
         */
        private BufferedImage convertToImage(double[][][] data) {
            int width = data.length;
            int height = data[0].length;
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // 各チャンネルの値を0-255の範囲にクリップしてピクセル値を生成
                    int r = (int) Math.max(0, Math.min(255, data[x][y][0]));
                    int g = (int) Math.max(0, Math.min(255, data[x][y][1]));
                    int b = (int) Math.max(0, Math.min(255, data[x][y][2]));
                    int rgb = (r << 16) | (g << 8) | b;
                    newImage.setRGB(x, y, rgb);
                }
            }
            return newImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); //
            if (image != null) { //
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null); //
            }
        }
    }

    /**
     * マウス情報を表示する機能を追加したImagePanel。
     */
    private class InfoImagePanel extends ImagePanel {
        private Point calculationPoint;
        private boolean altPressed;

        public InfoImagePanel(String title) {
            super(title);
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
                g.setFont(new Font("Monospaced", Font.BOLD, 14));
                String info = String.format("Last Click: (%d, %d)", calculationPoint.x, calculationPoint.y);
                g.drawString(info, 10, getHeight() - 25);
                String altInfo = String.format("Alt Key: %s", altPressed ? "ON" : "OFF");
                g.drawString(altInfo, 10, getHeight() - 10);
            }
        }
    }
}