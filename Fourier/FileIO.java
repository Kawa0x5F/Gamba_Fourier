package Fourier;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class FileIO {

     /**
	 * 離散フーリエ1次元変換のための元データ(自分で用意した信号(ファイル形式はCSV予定))。
	 */
    public static double[] readSignalFromCSV (String filePath) {
        List<Double> signalList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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

            double[][][] result = new double[width][height][3];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    result[x][y][0] = (double) r;
                    result[x][y][1] = (double) g;
                    result[x][y][2] = (double) b;
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
    public static void writeSignalToCSV (double[] signalData, File csvFile) { //ファイルの保存先の指定はメニューが行う予定
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (double value : signalData) {
                bw.write(Double.toString(value));
                bw.newLine();  // 改行して1列にする
            }
            System.out.println("CSVファイルへの書き出しが完了しました: " + csvFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("CSVファイルへの書き出しに失敗しました。");
        }            
    } 
    
     /**
     * 2次元データを保存
     */
    public static void writeSignalToImage (double[][][] imageData, File imageFile) {
        int width = imageData.length;
        int height = imageData[0].length;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = (int) Math.min(255, Math.max(0, imageData[x][y][0]));
                int g = (int) Math.min(255, Math.max(0, imageData[x][y][1]));
                int b = (int) Math.min(255, Math.max(0, imageData[x][y][2]));
                int rgb = (r << 16) | (g << 8) | b;
                bi.setRGB(x, y, rgb);
            }
        }

        String formatName = getImageFormat(imageFile);
        if (formatName == null) {
            System.out.println("未対応の拡張子です: " + imageFile.getName() + "。保存をスキップしました。");
            return;
        }


        try {
            ImageIO.write(bi, formatName, imageFile);
            System.out.println("画像ファイルへの書き出しが完了しました: " + imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("画像ファイルへの書き出しに失敗しました。");
        }
    } 

    /**
     * 画像ファイルの拡張子を取得
     * 対応していない拡張子の場合はnullを返す
     * 対応拡張子: jpg(jpeg), png, bmp, gif
     */
    private static String getImageFormat(File imageFile) {
        String name = imageFile.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "jpg";
        else if (name.endsWith(".png")) return "png";
        else if (name.endsWith(".bmp")) return "bmp";
        else if (name.endsWith(".gif")) return "gif";
        else return null;
    }
    
}    
    

