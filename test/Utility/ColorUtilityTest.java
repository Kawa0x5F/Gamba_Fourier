package Utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;

/**
 * ColorUtilityクラスのテストクラス
 */
class ColorUtilityTest {

    @Test
    @DisplayName("RGB値から色の変換テスト")
    void testColorFromRGB() {
        // 白色のテスト
        Color white = ColorUtility.colorFromRGB(1.0, 1.0, 1.0);
        assertEquals(255, white.getRed());
        assertEquals(255, white.getGreen());
        assertEquals(255, white.getBlue());

        // 黒色のテスト
        Color black = ColorUtility.colorFromRGB(0.0, 0.0, 0.0);
        assertEquals(0, black.getRed());
        assertEquals(0, black.getGreen());
        assertEquals(0, black.getBlue());

        // 赤色のテスト
        Color red = ColorUtility.colorFromRGB(1.0, 0.0, 0.0);
        assertEquals(255, red.getRed());
        assertEquals(0, red.getGreen());
        assertEquals(0, red.getBlue());
    }

    @Test
    @DisplayName("RGB配列から色の変換テスト")
    void testColorFromRGBArray() {
        double[] rgbArray = {0.5, 0.5, 0.5};
        Color color = ColorUtility.colorFromRGB(rgbArray);
        
        // 0.5 * 255 = 127.5 → 128
        assertEquals(128, color.getRed());
        assertEquals(128, color.getGreen());
        assertEquals(128, color.getBlue());
    }

    @Test
    @DisplayName("輝度から色の変換テスト")
    void testColorFromLuminance() {
        Color color = ColorUtility.colorFromLuminance(0.5);
        
        // グレースケール（R=G=B）
        assertEquals(128, color.getRed());
        assertEquals(128, color.getGreen());
        assertEquals(128, color.getBlue());
    }

    @Test
    @DisplayName("RGB値をINT値に変換テスト")
    void testConvertRGBtoINT() {
        // 白色
        int white = ColorUtility.convertRGBtoINT(1.0, 1.0, 1.0);
        assertEquals(0xFFFFFF, white);

        // 黒色
        int black = ColorUtility.convertRGBtoINT(0.0, 0.0, 0.0);
        assertEquals(0x000000, black);

        // 赤色
        int red = ColorUtility.convertRGBtoINT(1.0, 0.0, 0.0);
        assertEquals(0xFF0000, red);
    }

    @Test
    @DisplayName("RGB配列をINT値に変換テスト")
    void testConvertRGBArrayToINT() {
        double[] rgbArray = {1.0, 0.0, 0.0};
        int result = ColorUtility.convertRGBtoINT(rgbArray);
        assertEquals(0xFF0000, result);
    }

    @Test
    @DisplayName("INT値をRGB配列に変換テスト")
    void testConvertINTtoRGB() {
        double[] rgb = ColorUtility.convertINTtoRGB(0xFF0000);
        
        assertEquals(1.0, rgb[0], 0.01); // Red
        assertEquals(0.0, rgb[1], 0.01); // Green
        assertEquals(0.0, rgb[2], 0.01); // Blue
    }

    @Test
    @DisplayName("RGBからYUVへの変換テスト")
    void testConvertRGBtoYUV() {
        double[] yuv = ColorUtility.convertRGBtoYUV(1.0, 0.0, 0.0);
        
        // 赤色のYUV値
        assertEquals(0.299, yuv[0], 0.01); // Y
        assertEquals(-0.169, yuv[1], 0.01); // U
        assertEquals(0.5, yuv[2], 0.01); // V
    }

    @Test
    @DisplayName("RGB配列からYUVへの変換テスト")
    void testConvertRGBArrayToYUV() {
        double[] rgbArray = {1.0, 0.0, 0.0};
        double[] yuv = ColorUtility.convertRGBtoYUV(rgbArray);
        
        assertEquals(0.299, yuv[0], 0.01); // Y
        assertEquals(-0.169, yuv[1], 0.01); // U
        assertEquals(0.5, yuv[2], 0.01); // V
    }

    @Test
    @DisplayName("YUVからRGBへの変換テスト")
    void testConvertYUVtoRGB() {
        double[] rgb = ColorUtility.convertYUVtoRGB(0.299, -0.169, 0.5);
        
        // 赤色に近い値
        assertEquals(1.0, rgb[0], 0.1); // Red
        assertEquals(0.0, rgb[1], 0.1); // Green
        assertEquals(0.0, rgb[2], 0.1); // Blue
    }

    @Test
    @DisplayName("YUV配列からRGBへの変換テスト")
    void testConvertYUVArrayToRGB() {
        double[] yuvArray = {0.299, -0.169, 0.5};
        double[] rgb = ColorUtility.convertYUVtoRGB(yuvArray);
        
        assertEquals(1.0, rgb[0], 0.1); // Red
        assertEquals(0.0, rgb[1], 0.1); // Green
        assertEquals(0.0, rgb[2], 0.1); // Blue
    }

    @Test
    @DisplayName("YUVから色の変換テスト")
    void testColorFromYUV() {
        Color color = ColorUtility.colorFromYUV(0.299, -0.169, 0.5);
        
        // 赤色に近い値
        assertTrue(color.getRed() > 200);
        assertTrue(color.getGreen() < 50);
        assertTrue(color.getBlue() < 50);
    }

    @Test
    @DisplayName("RGB配列から輝度計算テスト")
    void testLuminanceFromRGBArray() {
        double[] whiteRGB = {1.0, 1.0, 1.0};
        double luminance = ColorUtility.luminanceFromRGB(whiteRGB);
        
        // 白色の輝度は高い
        assertTrue(luminance > 0.8);
    }

    @Test
    @DisplayName("INT値から輝度計算テスト")
    void testLuminanceFromINT() {
        int white = 0xFFFFFF;
        double luminance = ColorUtility.luminanceFromRGB(white);
        
        // 白色の輝度は高い
        assertTrue(luminance > 0.8);
    }

    @Test
    @DisplayName("YUV配列から輝度計算テスト")
    void testLuminanceFromYUVArray() {
        double[] yuvArray = {0.8, 0.0, 0.0};
        double luminance = ColorUtility.luminanceFromYUV(yuvArray);
        
        assertEquals(0.8, luminance, 0.01);
    }
}
