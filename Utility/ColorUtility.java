// 青木先生のjarファイルの逆コンパイル結果で得られたutility
package Utility;

import java.awt.Color;

public class ColorUtility {
   public ColorUtility() {
   }

   public static Color colorFromLuminance(double var0) {
      int var2 = convertRGBtoINT(var0, var0, var0);
      Color var3 = new Color(var2);
      return var3;
   }

   public static Color colorFromRGB(double[] var0) {
      double var1 = var0[0];
      double var3 = var0[1];
      double var5 = var0[2];
      return colorFromRGB(var1, var3, var5);
   }

   public static Color colorFromRGB(double var0, double var2, double var4) {
      int var6 = convertRGBtoINT(var0, var2, var4);
      Color var7 = new Color(var6);
      return var7;
   }

   public static Color colorFromYUV(double[] var0) {
      double[] var1 = convertYUVtoRGB(var0);
      Color var2 = colorFromRGB(var1);
      return var2;
   }

   public static Color colorFromYUV(double var0, double var2, double var4) {
      double[] var6 = new double[]{var0, var2, var4};
      return colorFromYUV(var6);
   }

   public static double[] convertINTtoRGB(int var0) {
      double var1 = (double)(var0 >> 16 & 255) / 255.0;
      double var3 = (double)(var0 >> 8 & 255) / 255.0;
      double var5 = (double)(var0 & 255) / 255.0;
      double[] var7 = new double[]{var1, var3, var5};
      return var7;
   }

   public static int convertRGBtoINT(double[] var0) {
      double var1 = var0[0];
      double var3 = var0[1];
      double var5 = var0[2];
      return convertRGBtoINT(var1, var3, var5);
   }

   public static int convertRGBtoINT(double var0, double var2, double var4) {
      int var6 = (int)Math.round(var0 * 255.0);
      int var7 = (int)Math.round(var2 * 255.0);
      int var8 = (int)Math.round(var4 * 255.0);
      var6 = var6 << 16 & 16711680;
      var7 = var7 << 8 & '\uff00';
      var8 &= 255;
      int var9 = var6 + var7 + var8;
      return var9;
   }

   public static double[] convertRGBtoYUV(double[] var0) {
      double var1 = var0[0];
      double var3 = var0[1];
      double var5 = var0[2];
      return convertRGBtoYUV(var1, var3, var5);
   }

   public static double[] convertRGBtoYUV(double var0, double var2, double var4) {
      double var6 = 0.299 * var0 + 0.587 * var2 + 0.114 * var4;
      double var8 = -0.169 * var0 + -0.331 * var2 + 0.5 * var4;
      double var10 = 0.5 * var0 + -0.419 * var2 + -0.081 * var4;
      return new double[]{var6, var8, var10};
   }

   public static double[] convertRGBtoYUV(int var0) {
      double[] var1 = convertINTtoRGB(var0);
      return convertRGBtoYUV(var1);
   }

   public static double[] convertYUVtoRGB(double[] var0) {
      double var1 = var0[0];
      double var3 = var0[1];
      double var5 = var0[2];
      return convertYUVtoRGB(var1, var3, var5);
   }

   public static double[] convertYUVtoRGB(double var0, double var2, double var4) {
      double var6 = 1.0 * var0 + 1.402 * var4;
      double var8 = 1.0 * var0 + -0.344 * var2 + -0.714 * var4;
      double var10 = 1.0 * var0 + 1.772 * var2;
      return new double[]{var6, var8, var10};
   }

   public static double luminanceFromRGB(double[] var0) {
      double[] var1 = convertRGBtoYUV(var0);
      return luminanceFromYUV(var1);
   }

   public static double luminanceFromRGB(int var0) {
      double[] var1 = convertRGBtoYUV(var0);
      return luminanceFromYUV(var1);
   }

   public static double luminanceFromYUV(double[] var0) {
      return var0[0];
   }
}
