package Fourier.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 画像を4つのグリッドで表示するウィンドウクラス。
 * 左上のグリッドに指定された画像を縦横比を保って表示します。
 */
public class ImageWindow extends JFrame {

    /**
     * 指定されたタイトルと画像でImageWindowを作成します。
     * @param title ウィンドウのタイトル
     * @param image 表示する画像
     */
    public ImageWindow(String title, BufferedImage image) {
        super(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 2));

        for (int i = 1; i <= 4; i++) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel numberLabel = new JLabel("Grid:"+String.valueOf(i));
            numberLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            numberLabel.setHorizontalAlignment(SwingConstants.LEFT);
            numberLabel.setVerticalAlignment(SwingConstants.TOP);
            panel.add(numberLabel, BorderLayout.NORTH);

            if (i == 1 && image != null) {
                // カスタムパネルで縦横比を維持して画像を描画
                panel.add(new AspectRatioImagePanel(image), BorderLayout.CENTER);
            }

            add(panel);
        }

        setVisible(true);
    }

    /**
     * 縦横比を保って画像を描画するパネルの内部クラス。
     */
    private static class AspectRatioImagePanel extends JPanel {
        private final BufferedImage image;

        /**
         * 指定された画像でAspectRatioImagePanelを作成します。
         * @param image 表示する画像
         */
        public AspectRatioImagePanel(BufferedImage image) {
            this.image = image;
        }

        /**
         * パネルのコンポーネントを描画します。
         * 画像を縦横比を保ってパネル内に描画します。
         * @param g 描画に使用するGraphicsオブジェクト
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();

                double panelAspect = (double) panelWidth / panelHeight;
                double imageAspect = (double) imgWidth / imgHeight;

                int drawWidth, drawHeight;
                if (panelAspect > imageAspect) {
                    // パネルが横長 → 高さに合わせて横を縮小
                    drawHeight = panelHeight;
                    drawWidth = (int) (drawHeight * imageAspect);
                } else {
                    // パネルが縦長 → 幅に合わせて縦を縮小
                    drawWidth = panelWidth;
                    drawHeight = (int) (drawWidth / imageAspect);
                }

                int x = (panelWidth - drawWidth) / 2;
                int y = (panelHeight - drawHeight) / 2;
                g.drawImage(image, x, y, drawWidth, drawHeight, this);
            }
        }
    }
}
