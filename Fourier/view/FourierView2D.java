package Fourier.view;

import Fourier.model.FourierModel2D;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FourierView2D extends FourierView implements PropertyChangeListener {

    private static final String KEY_ORIGINAL_IMAGE = "Original Image";
    private static final String KEY_ORIGINAL_SPECTRUM = "Original Power Spectrum (Log Scale)";
    private static final String KEY_RECONSTRUCTED_IMAGE = "Reconstructed Image";
    private static final String KEY_MODIFIED_SPECTRUM = "User Modified Power Spectrum (Log Scale)";

    public static final int PANEL_WIDTH = 400;
    public static final int PANEL_HEIGHT = 400;

    // --- コンストラクタを簡素化 ---
    public FourierView2D(FourierModel2D model, int creationIndex) {
        super(model, "2D Fourier Transform - Spectrum Manipulation");
        
        // パネルの生成と追加
        addPanel(KEY_ORIGINAL_IMAGE, new ImagePanel(KEY_ORIGINAL_IMAGE));
        addPanel(KEY_ORIGINAL_SPECTRUM, new ImagePanel(KEY_ORIGINAL_SPECTRUM));
        addPanel(KEY_RECONSTRUCTED_IMAGE, new ImagePanel(KEY_RECONSTRUCTED_IMAGE));
        addPanel(KEY_MODIFIED_SPECTRUM, new InfoImagePanel(KEY_MODIFIED_SPECTRUM));

        updateView();

        int offset = creationIndex * 30;
        frame.setLocation(offset, offset);

        // コンポーネントに基づいてフレームサイズを自動調整
        frame.pack();
        setVisible(true);
    }
    
    @Override
    protected void updateView() {
        FourierModel2D model2D = (FourierModel2D) getModel();

        ((ImagePanel) panels.get(KEY_ORIGINAL_IMAGE)).setData(model2D.getInitialOriginColorData());

        double[][] initialSpectrumData = model2D.getInitialPowerSpectrumData();
        double[][][] initialSpectrumAsColor = convertGrayDataToColorData(initialSpectrumData, true);
        ((ImagePanel) panels.get(KEY_ORIGINAL_SPECTRUM)).setData(initialSpectrumAsColor);

        ((ImagePanel) panels.get(KEY_RECONSTRUCTED_IMAGE)).setData(model2D.getIfftResultColorData());

        double[][] modifiedSpectrumData = model2D.getRecalculatedPowerSpectrumData();
        double[][][] modifiedSpectrumAsColor = convertGrayDataToColorData(modifiedSpectrumData, true);
        ((ImagePanel) panels.get(KEY_MODIFIED_SPECTRUM)).setData(modifiedSpectrumAsColor);
    }
    
    private double[][][] convertGrayDataToColorData(double[][] grayData, boolean useLogScale) {
        if (grayData == null || grayData.length == 0) return null;

        int height = grayData.length;
        int width = grayData[0].length;
        double[][][] colorData = new double[width][height][3];

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = useLogScale ? Math.log1p(grayData[y][x]) : grayData[y][x];
                if (val > max) max = val;
                if (val < min) min = val;
            }
        }

        double range = max - min;
        if (range == 0) range = 1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = useLogScale ? Math.log1p(grayData[y][x]) : grayData[y][x];
                double normalizedValue = 255 * (val - min) / range;
                colorData[x][y][0] = normalizedValue;
                colorData[x][y][1] = normalizedValue;
                colorData[x][y][2] = normalizedValue;
            }
        }
        return colorData;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt); // updateViewを呼び出す

        String propertyName = evt.getPropertyName();
        if (propertyName.equals("calculationPoint") || propertyName.equals("altKeyState")) {
            FourierModel2D model2D = (FourierModel2D) getModel();
            if (panels.get(KEY_MODIFIED_SPECTRUM) instanceof InfoImagePanel) {
                ((InfoImagePanel) panels.get(KEY_MODIFIED_SPECTRUM)).setCalculationInfo(
                    model2D.getLastCalculationPoint(),
                    model2D.getIsAltDown()
                );
            }
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
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
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