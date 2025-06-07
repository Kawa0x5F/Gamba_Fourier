// ImagePanel.java
package Fourier.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage image;

    public ImagePanel(BufferedImage image) {
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int cellWidth = panelWidth / 2;
        int cellHeight = panelHeight / 2;

        // Draw image in top-left cell (Grid 1)
        if (image != null) {
            g.drawImage(image, 0, 0, cellWidth, cellHeight, null);
        }

        // Draw grid lines and numbers
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
