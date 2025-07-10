package Fourier.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 画像を表示するパネルクラス。
 * 4つのグリッドを持ち、左上のグリッドに画像を表示します。
 */
public class ImagePanel extends JPanel {
    private BufferedImage image;

    /**
     * 指定された画像でImagePanelを作成します。
     * @param image 表示する画像
     */
    public ImagePanel(BufferedImage image) {
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
    }

    /**
     * パネルのコンポーネントを描画します。
     * 4つのグリッドを描画し、左上のグリッドに画像を表示します。
     * @param g 描画に使用するGraphicsオブジェクト
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int cellWidth = panelWidth / 2;
        int cellHeight = panelHeight / 2;

        // 左上のセルに画像を描画（グリッド1）
        if (image != null) {
            g.drawImage(image, 0, 0, cellWidth, cellHeight, null);
        }

        // グリッド線と番号を描画
        for (int i = 0; i < 4; i++) {
            int col = i % 2;
            int row = i / 2;
            int x = col * cellWidth;
            int y = row * cellHeight;

            g.setColor(Color.BLACK);
            g.drawRect(x, y, cellWidth, cellHeight);
            g.drawString("Grid " + (i + 1), x + 10, y + 20);
        }
    }
}
