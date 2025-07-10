package Fourier.view;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
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
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < 4; i++) {
            int col = i % 2;
            int row = i / 2;
            int x = col * cellWidth;
            int y = row * cellHeight;

            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, cellWidth, cellHeight);
            
            // グリッド番号を縁取り文字で描画
            String gridText = "Grid " + (i + 1);
            drawOutlinedString(g2, gridText, x + 10, y + 20, Color.BLACK, Color.WHITE, 1.5f);
        }
    }

    /**
     * 縁取り文字を描画するヘルパーメソッド
     * @param g2 Graphics2Dオブジェクト
     * @param text 描画するテキスト
     * @param x X座標
     * @param y Y座標
     * @param fillColor 文字の塗りつぶし色
     * @param outlineColor 縁取りの色
     * @param outlineWidth 縁取りの幅
     */
    private void drawOutlinedString(Graphics2D g2, String text, int x, int y, Color fillColor, Color outlineColor, float outlineWidth) {
        // 元のStrokeを保存
        Stroke originalStroke = g2.getStroke();
        
        // 縁取りを描画
        g2.setColor(outlineColor);
        g2.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // FontRenderContextを取得してTextLayoutを使用
        FontRenderContext frc = g2.getFontRenderContext();
        java.awt.font.TextLayout layout = new java.awt.font.TextLayout(text, g2.getFont(), frc);
        Shape textShape = layout.getOutline(AffineTransform.getTranslateInstance(x, y));
        g2.draw(textShape);
        
        // 文字の塗りつぶし
        g2.setColor(fillColor);
        g2.fill(textShape);
        
        // 元のStrokeに戻す
        g2.setStroke(originalStroke);
    }
}
