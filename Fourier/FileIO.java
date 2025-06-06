package Fourier;

import java.awt.image.BufferedImage;
import Utility.ImageUtility;

public class FileIO {

    public static double[] dataChirpSignal()
	{
		double pi = Math.PI;
		int sourceSize = 1024;
		double[] sourceData = new double[sourceSize];
		for (int i = 0; i < sourceSize; i++)
		{
			double value = 12.0d * Math.sin((double)i * (double)i * pi / 2.0d / (double)sourceSize);
			sourceData[i] = value;
		}
		return sourceData;
	}

	/**
	 * 離散フーリエ1次元変換のための元データ(のこぎり波:波形の見た目が鋸の歯のような信号)。
	 */
	public static double[] dataSawtoothWave()
	{
		int sourceSize = 1024;
		double[] sourceData = new double[sourceSize];
		for (int i = 0; i < sourceSize; i++)
		{
			double value = 12.0d * ((double)(i % 50) / 25.0d - 1.0d);
			sourceData[i] = value;
		}
		return sourceData;
	}

	/**
	 * 離散フーリエ1次元変換のための元データ(矩形波:二つのレベルの間を規則的かつ瞬間的に変化する信号)。
	 */
	public static double[] dataSquareWave()
	{
		double pi = Math.PI;
		int sourceSize = 1024;
		double[] sourceData = new double[sourceSize];
		for (int i = 0; i < sourceSize; i++)
		{
			double cos = Math.cos(10.0d * 2.0d * pi * ((double)i / (double)sourceSize));
			double value = 12.0d * (cos >= 0.0d ? 1.0d : -1.0d);
			sourceData[i] = value;
		}
		return sourceData;
	}

	/**
	 * 離散フーリエ1次元変換のための元データ(いくつかの正弦波と余弦波の合成波)。
	 */
	public static double[] dataSampleWave()
	{
		double pi = Math.PI;
		int sourceSize = 1024;
		double[] sourceData = new double[sourceSize];
		for (int i = 0; i < sourceSize; i++)
		{
			double cos1 = 6.0d * Math.cos(12.0d * 2.0d * pi * (double)i / (double)sourceSize);
			double sin1 = 4.0d * Math.sin( 5.0d * 2.0d * pi * (double)i / (double)sourceSize);
			double cos2 = 3.0d * Math.cos(24.0d * 2.0d * pi * (double)i / (double)sourceSize);
			double sin2 = 2.0d * Math.sin(10.0d * 2.0d * pi * (double)i / (double)sourceSize);
			double value = cos1 + sin1 + cos2 + sin2;
			sourceData[i] = value;
		}
		return sourceData;
	}

	/**
	 * 離散フーリエ1次元変換のための元データ(三角波:波形の見た目が三角形のような信号)。
	 */
	public static double[] dataTriangleWave()
	{
		boolean flag = false;
		int sourceSize = 1024;
		double[] sourceData = new double[sourceSize];
		for (int i = 0; i < sourceSize; i++)
		{
			if (i % 50 == 0) { flag = flag ? false : true ; }
			double value = 12.0d * ((double)(i % 50) / 25.0d - 1.0d);
			if (flag) { value = 0.0d - value;}
			sourceData[i] = value;
		}
		return sourceData;
	}

    /**
	 * 離散フーリエ1次元変換のための元データ(自分で用意した信号(ファイル形式はCSV予定))。
	 */

	/**
	 * 離散フーリエ2次元変換のための元データ(4×4の2次元配列)。
	 */
	public static double[][] data4x4()
	{
		double[][] sourceData = new double[][] { { 900, 901, 902, 903 }, { 910, 911, 912, 913 }, { 920, 921, 922, 923 }, { 930, 931, 932, 933 } };
		return sourceData;
	}

	/**
	 * 離散フーリエ2次元変換のための元データ(ジョゼフ・フーリエさんのカラー画像)。
	 */
	public static double[][][] dataFourierColor()
	{
		BufferedImage anImage = ImageUtility.readImage("SampleImages/JosephFourier2.jpg");
		double[][][] yuvMatrixes = ImageUtility.convertImageToYUVMatrixes(anImage);
		return yuvMatrixes;
	}

    /**
	 * 離散フーリエ2次元変換のための元データ(自分で用意した画像)。
	 */

    /**
     * 1次元パワースペクトルのデータを保存
     */

     /**
     * 1次元操作後パワースペクトルのデータを保存
     */

    /**
     * 復元1次元信号のデータを保存
     */

     /**
     * 2次元パワースペクトルのデータを保存
     */

     /**
     * 2次元操作後パワースペクトルのデータを保存
     */

    /**
     * 復元2次元信号のデータを保存
     */

}
