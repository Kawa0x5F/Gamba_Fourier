package Fourier;

import java.awt.image.BufferedImage;
import Utility.ImageUtility;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class FileIO {

    public static void main(String[] args) {
        // CSV読み込みテスト
        String csvPath = "step_signal.csv";  // ファイルの入力方法はメニューと相談
        double[] csvResult = readSignalFromCSV(csvPath);

        if (csvResult.length == 0) {
            System.out.println("CSVの読み込みに失敗または空データです。");
        } else {
            System.out.println("CSV読み込み結果:");
            for (double v : csvResult) {
                System.out.println(v);
            }
        }

        // 画像読み込みテスト
        String imagePath = "JosephFourier1.jpg";  // ファイルの入力方法はメニューと相談
        double[][][] imageResult = readSignalFromImage(imagePath);

        if (imageResult == null) {
            System.out.println("画像の読み込みに失敗しました。");
        } else {
            System.out.println("画像読み込み結果 (一部):");
            System.out.printf("高さ: %d, 幅: %d, RGBチャンネル数: %d\n",
                imageResult.length, imageResult[0].length, imageResult[0][0].length);

            // 例えば左上ピクセルのRGBを表示
            System.out.printf("左上ピクセル RGB = (%.0f, %.0f, %.0f)\n",
                imageResult[0][0][0], imageResult[0][0][1], imageResult[0][0][2]);
        }
    }
    
    /**
	 * 離散フーリエ1次元変換のための元データ(自分で用意した信号(ファイル形式はCSV予定))。
	 */
    public static double[] readSignalFromCSV (String filePath) {
        List<Double> signalList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();  // 前後の空白を除去
                if (!line.isEmpty()) {
                    signalList.add(Double.parseDouble(line));
                }
            }
            
        } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
                return new double[0];
        }

        // Listからdouble[]へ変換
        double[] result = new double[signalList.size()];
        for (int i = 0; i < signalList.size(); i++) {
            result[i] = signalList.get(i);
        }   

        return result;
    }
    

    /**
	 * 離散フーリエ2次元変換のための元データ(自分で用意した画像)。
	 */
    public static double[][][] readSignalFromImage (String filePath) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            int width = image.getWidth();
            int height = image.getHeight();

            double[][][] result = new double[height][width][3];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    result[y][x][0] = (double) r;
                    result[y][x][1] = (double) g;
                    result[y][x][2] = (double) b;
                }
            }
            return result;
            
        } catch (IOException e) {
            /*
             * エラー処理について
             * 開発中はSystem.err.printlnで、実装時はダイアログ表示等に変更
             */
            System.err.println("画像の読み込みに失敗しました: " + e.getMessage()); 
            return null;
        }
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
