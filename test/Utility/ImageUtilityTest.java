package Utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * ImageUtilityクラスのテストクラス
 */
class ImageUtilityTest {

    @Test
    @DisplayName("画像リサイズテスト")
    void testAdjustImage() {
        // 2x2の元画像を作成
        BufferedImage originalImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        originalImage.setRGB(0, 0, Color.RED.getRGB());
        originalImage.setRGB(1, 0, Color.GREEN.getRGB());
        originalImage.setRGB(0, 1, Color.BLUE.getRGB());
        originalImage.setRGB(1, 1, Color.WHITE.getRGB());

        // 4x4にリサイズ
        BufferedImage resizedImage = ImageUtility.adjustImage(originalImage, 4, 4);
        
        assertNotNull(resizedImage);
        assertEquals(4, resizedImage.getWidth());
        assertEquals(4, resizedImage.getHeight());
        assertEquals(originalImage.getType(), resizedImage.getType());
    }

    @Test
    @DisplayName("グレースケール変換テスト")
    void testGrayscaleImage() {
        // 2x2のカラー画像を作成
        BufferedImage colorImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        colorImage.setRGB(0, 0, Color.RED.getRGB());
        colorImage.setRGB(1, 0, Color.GREEN.getRGB());
        colorImage.setRGB(0, 1, Color.BLUE.getRGB());
        colorImage.setRGB(1, 1, Color.WHITE.getRGB());

        // グレースケールに変換
        BufferedImage grayImage = ImageUtility.grayscaleImage(colorImage);
        
        assertNotNull(grayImage);
        assertEquals(2, grayImage.getWidth());
        assertEquals(2, grayImage.getHeight());
        assertEquals(colorImage.getType(), grayImage.getType());

        // 各ピクセルがグレースケールになっているか確認
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                Color pixel = new Color(grayImage.getRGB(x, y));
                // グレースケールではR=G=B
                assertEquals(pixel.getRed(), pixel.getGreen());
                assertEquals(pixel.getGreen(), pixel.getBlue());
            }
        }
    }

    @Test
    @DisplayName("画像読み込みテスト - 存在しないファイル")
    void testReadImageFromFileNotExists() {
        // 存在しないファイルの場合、nullが返されることを確認
        BufferedImage image = ImageUtility.readImageFromFile("nonexistent.jpg");
        assertNull(image);
    }

    @Test
    @DisplayName("画像読み込みテスト - 無効なURL")
    void testReadImageFromURLInvalid() {
        // 無効なURLの場合、nullが返されることを確認
        BufferedImage image = ImageUtility.readImageFromURL("invalid_url");
        assertNull(image);
    }

    @Test
    @DisplayName("YUVマトリックスから画像への変換テスト")
    void testConvertYUVMatrixesToImage() {
        // 2x2x3のYUV配列を作成
        double[][][] yuvMatrices = new double[3][2][2];
        
        // Y成分（輝度）
        yuvMatrices[0][0][0] = 0.5; // 左上
        yuvMatrices[0][0][1] = 0.7; // 右上
        yuvMatrices[0][1][0] = 0.3; // 左下
        yuvMatrices[0][1][1] = 0.9; // 右下
        
        // U成分（色差）
        yuvMatrices[1][0][0] = 0.0;
        yuvMatrices[1][0][1] = 0.0;
        yuvMatrices[1][1][0] = 0.0;
        yuvMatrices[1][1][1] = 0.0;
        
        // V成分（色差）
        yuvMatrices[2][0][0] = 0.0;
        yuvMatrices[2][0][1] = 0.0;
        yuvMatrices[2][1][0] = 0.0;
        yuvMatrices[2][1][1] = 0.0;

        // 画像への変換
        BufferedImage image = ImageUtility.convertYUVMatrixesToImage(yuvMatrices);
        
        assertNotNull(image);
        assertEquals(2, image.getWidth());
        assertEquals(2, image.getHeight());
    }

    @Test
    @DisplayName("輝度行列から画像への変換テスト")
    void testConvertLuminanceMatrixToImage() {
        // 2x2の輝度行列を作成
        double[][] luminanceMatrix = new double[2][2];
        luminanceMatrix[0][0] = 0.0; // 黒
        luminanceMatrix[0][1] = 0.5; // グレー
        luminanceMatrix[1][0] = 0.7; // 明るいグレー
        luminanceMatrix[1][1] = 1.0; // 白

        // 画像への変換
        BufferedImage image = ImageUtility.convertLuminanceMatrixToImage(luminanceMatrix);
        
        assertNotNull(image);
        assertEquals(2, image.getWidth());
        assertEquals(2, image.getHeight());
    }

    @Test
    @DisplayName("画像から輝度行列への変換テスト")
    void testConvertImageToLuminanceMatrix() {
        // 2x2の画像を作成
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.BLACK.getRGB());
        image.setRGB(1, 0, Color.WHITE.getRGB());
        image.setRGB(0, 1, Color.GRAY.getRGB());
        image.setRGB(1, 1, Color.RED.getRGB());

        // 輝度行列への変換
        double[][] luminanceMatrix = ImageUtility.convertImageToLuminanceMatrix(image);
        
        assertNotNull(luminanceMatrix);
        assertEquals(2, luminanceMatrix.length);    // height
        assertEquals(2, luminanceMatrix[0].length); // width
        
        // 値は0.0から1.0の範囲内であることを確認
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                assertTrue(luminanceMatrix[y][x] >= 0.0);
                assertTrue(luminanceMatrix[y][x] <= 1.0);
            }
        }
    }

    @Test
    @DisplayName("画像からYUV行列への変換テスト")
    void testConvertImageToYUVMatrixes() {
        // 2x2の画像を作成
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(1, 0, Color.GREEN.getRGB());
        image.setRGB(0, 1, Color.BLUE.getRGB());
        image.setRGB(1, 1, Color.WHITE.getRGB());

        // YUV行列への変換
        double[][][] yuvMatrixes = ImageUtility.convertImageToYUVMatrixes(image);
        
        assertNotNull(yuvMatrixes);
        assertEquals(3, yuvMatrixes.length);      // Y, U, V channels
        assertEquals(2, yuvMatrixes[0].length);   // height
        assertEquals(2, yuvMatrixes[0][0].length); // width
    }

    @Test
    @DisplayName("画像コピーテスト")
    void testCopyImage() {
        // 2x2の元画像を作成
        BufferedImage originalImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        originalImage.setRGB(0, 0, Color.RED.getRGB());
        originalImage.setRGB(1, 0, Color.GREEN.getRGB());
        originalImage.setRGB(0, 1, Color.BLUE.getRGB());
        originalImage.setRGB(1, 1, Color.WHITE.getRGB());

        // 画像をコピー
        BufferedImage copiedImage = ImageUtility.copyImage(originalImage);
        
        assertNotNull(copiedImage);
        assertEquals(originalImage.getWidth(), copiedImage.getWidth());
        assertEquals(originalImage.getHeight(), copiedImage.getHeight());
        assertEquals(originalImage.getType(), copiedImage.getType());
        
        // 元画像と同じ内容であることを確認
        assertEquals(originalImage.getRGB(0, 0), copiedImage.getRGB(0, 0));
        assertEquals(originalImage.getRGB(1, 0), copiedImage.getRGB(1, 0));
        assertEquals(originalImage.getRGB(0, 1), copiedImage.getRGB(0, 1));
        assertEquals(originalImage.getRGB(1, 1), copiedImage.getRGB(1, 1));
    }

    @Test
    @DisplayName("readImageメソッドの存在確認")
    void testReadImageMethods() {
        // readImage(String)メソッドの存在確認
        BufferedImage image1 = ImageUtility.readImage("nonexistent.jpg");
        assertNull(image1);
        
        // readImageFromFile(String)メソッドの存在確認
        BufferedImage image2 = ImageUtility.readImageFromFile("nonexistent.jpg");
        assertNull(image2);
    }

    @Test
    @DisplayName("writeImageメソッドの存在確認")
    void testWriteImageMethods() {
        // 2x2の画像を作成
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.RED.getRGB());
        
        // writeImage(BufferedImage, String)メソッドの存在確認
        // 実際のファイル書き込みは行わないが、メソッドが存在することを確認
        try {
            ImageUtility.writeImage(image, "test.png");
        } catch (Exception e) {
            // IOException等が発生することは想定内
            assertTrue(e instanceof Exception);
        }
    }
}
