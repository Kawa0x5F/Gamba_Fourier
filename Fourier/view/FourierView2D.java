package Fourier.view;

import Fourier.model.FourierModel2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

/**
 * 2次元フーリエ変換の結果を表示するビュークラス。
 * オリジナル画像、パワースペクトル、再構成画像、ユーザー変更スペクトルの4つのパネルを表示します。
 */
public class FourierView2D extends FourierView {

    private static final String KEY_ORIGINAL_IMAGE = "Original Image";
    private static final String KEY_ORIGINAL_SPECTRUM = "Original Power Spectrum (Log Scale)";
    private static final String KEY_RECONSTRUCTED_IMAGE = "Reconstructed Image";
    private static final String KEY_MODIFIED_SPECTRUM = "User Modified Power Spectrum (Log Scale)";
    
    private int imageWidth;
    private int imageHeight;
    private int displayWidth;
    private int displayHeight;

    private double initialSpectrumLogMin;
    private double initialSpectrumLogMax;

    /**
     * 2次元フーリエ変換ビューを作成します。
     * @param model 2次元フーリエ変換モデル
     * @param creationIndex ウィンドウ作成インデックス
     */
    public FourierView2D(FourierModel2D model, int creationIndex) {
        super(model, "2D Fourier Transform - Spectrum Manipulation");
        
        // 画像サイズを取得して表示サイズを計算
        double[][][] initialColorData = model.getInitialOriginColorData();
        this.imageWidth = initialColorData.length;
        this.imageHeight = initialColorData[0].length;
        calculateDisplaySize();
        
        // モデルに表示サイズを設定
        model.setDisplaySize(displayWidth, displayHeight);
        
        addPanel(KEY_ORIGINAL_IMAGE, new ImagePanel(KEY_ORIGINAL_IMAGE));
        addPanel(KEY_ORIGINAL_SPECTRUM, new ImagePanel(KEY_ORIGINAL_SPECTRUM));
        addPanel(KEY_RECONSTRUCTED_IMAGE, new ImagePanel(KEY_RECONSTRUCTED_IMAGE));
        addPanel(KEY_MODIFIED_SPECTRUM, new InfoImagePanel(KEY_MODIFIED_SPECTRUM));
        
        calculateAndStoreInitialSpectrumRange(model);
        
        updateView();

        int offset = creationIndex * 30;
        frame.setLocation(offset, offset);

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

        // [変更点] モデルの最新の状態から都度スペクトルを計算・取得する（重い処理）
        double[][] modifiedSpectrumData = model2D.generateCurrentPowerSpectrum();
        double[][][] modifiedSpectrumAsColor = convertGrayDataToColorDataWithFixedRange(modifiedSpectrumData, true);
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
    
    private double[][][] convertGrayDataToColorDataWithFixedRange(double[][] grayData, boolean useLogScale) {
        if (grayData == null || grayData.length == 0) return null;

        int height = grayData.length;
        int width = grayData[0].length;
        double[][][] colorData = new double[width][height][3];

        double min = this.initialSpectrumLogMin;
        double max = this.initialSpectrumLogMax;
        
        double range = max - min;
        if (range == 0) range = 1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = useLogScale ? Math.log1p(grayData[y][x]) : grayData[y][x];
                
                double normalizedValue = 255 * (val - min) / range;
                normalizedValue = Math.max(0, Math.min(255, normalizedValue));
                
                colorData[x][y][0] = normalizedValue;
                colorData[x][y][1] = normalizedValue;
                colorData[x][y][2] = normalizedValue;
            }
        }
        return colorData;
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
    }

    protected class ImagePanel extends SignalPanel {
        private BufferedImage image;

        public ImagePanel(String title) {
            super(title);
            this.setPreferredSize(new Dimension(displayWidth, displayHeight));
        }

        public void setData(double[][][] colorData) {
            this.image = (colorData == null) ? null : convertToImage(colorData);
            repaint();
        }
        
        private BufferedImage convertToImage(double[][][] data) {
            int width = data.length;
            int height = data[0].length;
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
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
                // 縦横比を保って中央に描画
                int panelWidth = this.getWidth();
                int panelHeight = this.getHeight();
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();
                
                // 縦横比を保ったサイズを計算
                double scaleX = (double) panelWidth / imgWidth;
                double scaleY = (double) panelHeight / imgHeight;
                double scale = Math.min(scaleX, scaleY);
                
                int drawWidth = (int) (imgWidth * scale);
                int drawHeight = (int) (imgHeight * scale);
                
                // 中央に配置するためのオフセットを計算
                int x = (panelWidth - drawWidth) / 2;
                int y = (panelHeight - drawHeight) / 2;
                
                g.drawImage(image, x, y, drawWidth, drawHeight, null);
            }
        }
    }

    private class InfoImagePanel extends ImagePanel {
        public InfoImagePanel(String title) {
            super(title);
        }
    }
    
    private void calculateAndStoreInitialSpectrumRange(FourierModel2D model) {
        double[][] initialSpectrum = model.getInitialPowerSpectrumData();
        if (initialSpectrum == null || initialSpectrum.length == 0) {
            this.initialSpectrumLogMin = 0.0;
            this.initialSpectrumLogMax = 1.0;
            return;
        }

        double maxVal = Double.NEGATIVE_INFINITY;
        double minVal = Double.POSITIVE_INFINITY;

        for (int y = 0; y < initialSpectrum.length; y++) {
            for (int x = 0; x < initialSpectrum[0].length; x++) {
                double logVal = Math.log1p(initialSpectrum[y][x]);
                if (logVal > maxVal) maxVal = logVal;
                if (logVal < minVal) minVal = logVal;
            }
        }
        this.initialSpectrumLogMin = minVal;
        this.initialSpectrumLogMax = maxVal;
    }
    
    /**
     * 画像サイズに基づいて適切な表示サイズを計算する
     */
    private void calculateDisplaySize() {
        // 最大表示サイズを設定（画面サイズを考慮）
        int maxDisplaySize = 300;
        
        // 縦横比を保ちながら適切なサイズを計算
        double aspectRatio = (double) imageWidth / imageHeight;
        
        if (aspectRatio >= 1.0) {
            // 横長または正方形の場合
            this.displayWidth = Math.min(maxDisplaySize, imageWidth);
            this.displayHeight = (int) (displayWidth / aspectRatio);
        } else {
            // 縦長の場合
            this.displayHeight = Math.min(maxDisplaySize, imageHeight);
            this.displayWidth = (int) (displayHeight * aspectRatio);
        }
        
        // 最小サイズを保証
        this.displayWidth = Math.max(this.displayWidth, 150);
        this.displayHeight = Math.max(this.displayHeight, 150);
    }
}