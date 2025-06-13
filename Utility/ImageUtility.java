// 青木先生のjarファイルの逆コンパイル結果で得られたutility
package Utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageUtility {
   public ImageUtility() {
   }

   public static BufferedImage adjustImage(BufferedImage var0, int var1, int var2) {
      BufferedImage var3 = new BufferedImage(var1, var2, var0.getType());
      Graphics2D var4 = var3.createGraphics();
      var4.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      var4.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      var4.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      var4.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
      var4.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      var4.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      var4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      var4.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      var4.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
      var4.drawImage(var0, 0, 0, var1, var2, (ImageObserver)null);
      return var3;
   }

   public static BufferedImage grayscaleImage(BufferedImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      BufferedImage var3 = new BufferedImage(var0.getWidth(), var0.getHeight(), var0.getType());

      for(int var4 = 0; var4 < var2; ++var4) {
         for(int var5 = 0; var5 < var1; ++var5) {
            int var6 = var0.getRGB(var5, var4);
            double var7 = ColorUtility.luminanceFromRGB(var6);
            var6 = ColorUtility.convertRGBtoINT(var7, var7, var7);
            var3.setRGB(var5, var4, var6);
         }
      }

      return var3;
   }

   public static BufferedImage copyImage(BufferedImage var0) {
      BufferedImage var1 = new BufferedImage(var0.getWidth(), var0.getHeight(), var0.getType());
      Graphics2D var2 = var1.createGraphics();
      var2.setColor(Color.white);
      var2.fillRect(0, 0, var1.getWidth(), var1.getHeight());
      var2.drawImage(var0, 0, 0, (ImageObserver)null);
      return var1;
   }

   public static double[][] convertImageToLuminanceMatrix(BufferedImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      double[][] var3 = new double[var2][var1];

      for(int var4 = 0; var4 < var2; ++var4) {
         for(int var5 = 0; var5 < var1; ++var5) {
            int var6 = var0.getRGB(var5, var4);
            var3[var4][var5] = ColorUtility.luminanceFromRGB(var6);
         }
      }

      return var3;
   }

   public static double[][][] convertImageToYUVMatrixes(BufferedImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      double[][] var3 = new double[var2][var1];
      double[][] var4 = new double[var2][var1];
      double[][] var5 = new double[var2][var1];

      for(int var6 = 0; var6 < var2; ++var6) {
         for(int var7 = 0; var7 < var1; ++var7) {
            int var8 = var0.getRGB(var7, var6);
            double[] var9 = ColorUtility.convertRGBtoYUV(var8);
            var3[var6][var7] = var9[0];
            var4[var6][var7] = var9[1];
            var5[var6][var7] = var9[2];
         }
      }

      return new double[][][]{var3, var4, var5};
   }

   public static BufferedImage convertLuminanceMatrixToImage(double[][] var0) {
      int var1 = var0[0].length;
      int var2 = var0.length;
      BufferedImage var3 = new BufferedImage(var1, var2, 1);
      Graphics2D var4 = var3.createGraphics();
      var4.setColor(Color.white);
      var4.fillRect(0, 0, var1, var2);

      for(int var5 = 0; var5 < var2; ++var5) {
         for(int var6 = 0; var6 < var1; ++var6) {
            double var7 = var0[var5][var6];
            double[] var9 = ColorUtility.convertYUVtoRGB(var7, 0.0, 0.0);
            int var10 = ColorUtility.convertRGBtoINT(var9);
            var3.setRGB(var6, var5, var10);
         }
      }

      return var3;
   }

   public static BufferedImage convertYUVMatrixesToImage(double[][][] var0) {
      double[][] var1 = var0[0];
      double[][] var2 = var0[1];
      double[][] var3 = var0[2];
      int var4 = var1[0].length;
      int var5 = var1.length;
      BufferedImage var6 = new BufferedImage(var4, var5, 1);
      Graphics2D var7 = var6.createGraphics();
      var7.setColor(Color.white);
      var7.fillRect(0, 0, var4, var5);

      for(int var8 = 0; var8 < var5; ++var8) {
         for(int var9 = 0; var9 < var4; ++var9) {
            double[] var10 = ColorUtility.convertYUVtoRGB(var1[var8][var9], var2[var8][var9], var3[var8][var9]);
            int var11 = ColorUtility.convertRGBtoINT(var10);
            var6.setRGB(var9, var8, var11);
         }
      }

      return var6;
   }

   public static BufferedImage readImage(File var0) {
      return readImageFromFile(var0);
   }

   public static BufferedImage readImage(String var0) {
      return readImageFromFile(var0);
   }

   public static BufferedImage readImageFromFile(File var0) {
      BufferedImage var1 = null;

      try {
         var1 = ImageIO.read(var0);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return var1;
   }

   public static BufferedImage readImageFromFile(String var0) {
      File var1 = new File(var0);
      return readImageFromFile(var1);
   }

   public static BufferedImage readImageFromURL(URL var0) {
      BufferedImage var1 = null;

      try {
         var1 = ImageIO.read(var0);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return var1;
   }

   public static BufferedImage readImageFromURL(String var0) {
      URL var1 = null;

      try {
         var1 = new URL(var0);
      } catch (MalformedURLException var3) {
         var3.printStackTrace();
      }

      return readImageFromURL(var1);
   }

   public static void writeImage(BufferedImage var0, File var1) {
      String var2 = var1.getName();
      var2 = var2.substring(var2.lastIndexOf(".") + 1);

      try {
         ImageIO.write(var0, var2, var1);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public static void writeImage(BufferedImage var0, String var1) {
      File var2 = new File(var1);
      writeImage(var0, var2);
   }
}
