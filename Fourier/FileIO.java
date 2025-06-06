package Fourier;

import java.io.File;

public class FileIO {
	
    /**
	 * 離散フーリエ1次元変換のための元データ(自分で用意した信号(ファイル形式はCSV予定))。
	 */
    public static double[] readSignalFromCSV (File csvFile) {
        return null;
    } 
    

    /**
	 * 離散フーリエ2次元変換のための元データ(自分で用意した画像)。
	 */
    public static double[][][] readSignalFromImage (File imageFile) {
        return null;
    } 

    
     /**
     * 1次元データを保存
     */
    public static void writeSignalToCSV (double[] signalData, File csvFile) {
        return;
    } 
    
     /**
     * 2次元データを保存
     */
    public static void writeSignalToImage (double[][][] imageData, File imageFile) {
        return;
    } 
    
}
