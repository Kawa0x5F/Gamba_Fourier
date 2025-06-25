package Fourier.view;

import Fourier.model.FourierModel2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FourierView2D extends FourierView implements PropertyChangeListener {

    // 各パネルを識別するためのキーとタイトル
    private static final String KEY_ORIGINAL_IMAGE = "Original Image";
    private static final String KEY_ORIGINAL_SPECTRUM = "Original Power Spectrum (Log Scale)";
    private static final String KEY_RECONSTRUCTED_IMAGE = "Reconstructed Image";
    private static final String KEY_MODIFIED_SPECTRUM = "User Modified Power Spectrum (Log Scale)";

    // Modelでも参照されるためpublic static
    public static final int PANEL_WIDTH = 400;
    public static final int PANEL_HEIGHT = 400;

    public FourierView2D(FourierModel2D model) {
        super(model, "2D Fourier Transform - Spectrum Manipulation");

        frame.setSize(850, 850); // 画面サイズを調整
        frame.setLayout(new GridLayout(2, 2, 5, 5)); // 2x2グリッド

        // パネルの追加とタイトル設定
        // パワースペクトル表示用のパネルは対数スケール表示を有効にする
        addPanel(KEY_ORIGINAL_IMAGE, new ImagePanel(KEY_ORIGINAL_IMAGE, false)); // 左上: 元の画像
        addPanel(KEY_ORIGINAL_SPECTRUM, new ImagePanel(KEY_ORIGINAL_SPECTRUM, true)); // 右上: 元のパワースペクトル
        addPanel(KEY_RECONSTRUCTED_IMAGE, new ImagePanel(KEY_RECONSTRUCTED_IMAGE, false)); // 左下: IFFT再構成画像
        
        // 右下のパネルはマウス情報を表示できるInfoImagePanelを使用
        addPanel(KEY_MODIFIED_SPECTRUM, new InfoImagePanel(KEY_MODIFIED_SPECTRUM, true));

        model.addPropertyChangeListener(this);
        updateView(); // 初回表示の更新
        setVisible(true);
    }

    @Override
    protected void updateView() {
        FourierModel2D model2D = (FourierModel2D) getModel();

        // 1. 元の画像 (initialOriginData) - 左上
        ((ImagePanel) panels.get(KEY_ORIGINAL_IMAGE)).setData(model2D.getInitialOriginData());

        // 2. 元のパワースペクトル - 右上
        // このゲッターはModel側で追加実装されていることを想定
        // ((ImagePanel) panels.get(KEY_ORIGINAL_SPECTRUM)).setData(model2D.getInitialPowerSpectrumData());

        // 3. IFFTで再構成された画像 (ifftResultData) - 左下
        ((ImagePanel) panels.get(KEY_RECONSTRUCTED_IMAGE)).setData(model2D.getIfftResultData());

        // 4. ユーザー操作によって再計算されたパワースペクトル (recalculatedPowerSpectrumData) - 右下
        ((ImagePanel) panels.get(KEY_MODIFIED_SPECTRUM)).setData(model2D.getRecalculatedPowerSpectrumData());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        // データが更新されたら、関連するパネルを含むビュー全体を更新
        switch (propertyName) {
            case "userModifiedSpectrumData":
            case "recalculatedPowerSpectrumData":
            case "ifftResultData":
            // case "initialPowerSpectrumData": // 初期データ更新時
                updateView();
                break;
            
            // 計算情報パネルのみの更新
            case "calculationPoint":
            case "altKeyState":
                FourierModel2D model2D = (FourierModel2D) getModel();
                Point calcPoint = model2D.getLastCalculationPoint();
                boolean altDown = model2D.getIsAltDown();

                if (panels.get(KEY_MODIFIED_SPECTRUM) instanceof InfoImagePanel) {
                    ((InfoImagePanel) panels.get(KEY_MODIFIED_SPECTRUM)).setCalculationInfo(calcPoint, altDown);
                }
                break;
        }
    }

    // 2次元データをグレースケール画像として表示するパネル
    protected class ImagePanel extends SignalPanel {
        private BufferedImage image;
        private final boolean useLogScale;

        public ImagePanel(String title, boolean useLogScale) {
            super(title);
            this.useLogScale = useLogScale;
            this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        }

        public void setData(double[][] data) {
            if (data == null || data.length == 0 || data[0].length == 0) {
                this.image = null;
            } else {
                this.image = convertToImage(data);
            }
            repaint();
        }

        private BufferedImage convertToImage(double[][] data) {
            int width = data[0].length;
            int height = data.length;
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;

            // データの最大値・最小値を探す
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double val = useLogScale ? Math.log1p(data[y][x]) : data[y][x];
                    if (val > max) max = val;
                    if (val < min) min = val;
                }
            }

            double range = max - min;
            if (range == 0) range = 1; // 全て同じ値の場合のゼロ除算を防止

            // データを0-255の輝度値に正規化して画像にセット
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double val = useLogScale ? Math.log1p(data[y][x]) : data[y][x];
                    int gray = (int) (255 * (val - min) / range);
                    int rgb = (gray << 16) | (gray << 8) | gray;
                    newImage.setRGB(x, y, rgb);
                }
            }
            return newImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // 親クラスの描画（タイトルなど）
            if (image != null) {
                // パネルサイズに合わせて画像を描画
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
            }
        }
    }

    // マウス情報を表示する機能を追加したImagePanel
    private class InfoImagePanel extends ImagePanel {
        private Point calculationPoint;
        private boolean altPressed;

        public InfoImagePanel(String title, boolean useLogScale) {
            super(title, useLogScale);
        }

        public void setCalculationInfo(Point point, boolean alt) {
            this.calculationPoint = point;
            this.altPressed = alt;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // ImagePanelの描画処理を呼び出す
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