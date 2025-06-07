// ImageUtility.java
package Fourier.view;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageUtility {
    public static BufferedImage readImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double[][][] convertImageToYUVMatrixes(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][][] yuv = new double[3][height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb);

                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();

                double Y = 0.299 * r + 0.587 * g + 0.114 * b;
                double U = -0.14713 * r - 0.28886 * g + 0.436 * b;
                double V = 0.615 * r - 0.51499 * g - 0.10001 * b;

                yuv[0][y][x] = Y;
                yuv[1][y][x] = U;
                yuv[2][y][x] = V;
            }
        }
        return yuv;
    }

    public static BufferedImage convertYUVToImage(double[][][] yuv) {
        int height = yuv[0].length;
        int width = yuv[0][0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double Y = yuv[0][y][x];
                double U = yuv[1][y][x];
                double V = yuv[2][y][x];

                int r = (int)(Y + 1.13983 * V);
                int g = (int)(Y - 0.39465 * U - 0.58060 * V);
                int b = (int)(Y + 2.03211 * U);

                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));

                Color color = new Color(r, g, b);
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }
    public static double[][] convertImageToLuminanceMatrix(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    double[][] luminance = new double[height][width];

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int rgb = image.getRGB(x, y);
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            // 輝度（luminance）を求める：ITU-R BT.601 の係数
            luminance[y][x] = 0.299 * r + 0.587 * g + 0.114 * b;
        }
    }
    return luminance;
}
public static BufferedImage convertLuminanceMatrixToImage(double[][] matrix) {
    int height = matrix.length;
    int width = matrix[0].length;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int value = (int)Math.round(matrix[y][x]);
            value = Math.max(0, Math.min(255, value)); // 範囲チェック
            int rgb = (value << 16) | (value << 8) | value;
            image.setRGB(x, y, rgb);
        }
    }
    return image;
}


    
}
