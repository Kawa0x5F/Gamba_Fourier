package Fourier;

import java.awt.image.BufferedImage;
import java.io.*; // File, FileReader, BufferedReaderなどをインポート
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class FileIO {
    /**
     * CSVファイルから1次元信号を読み込みます。
     * @param filePath 読み込むCSVファイルの絶対パス
     */
    public static double[] readSignalFromCSV(String filePath) {
        List<Double> signalList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    signalList.add(Double.parseDouble(line.trim()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null; // エラー時はnullを返す
        }
        return signalList.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * 画像ファイルから2次元信号(RGB)を読み込みます。
     * @param filePath 読み込む画像ファイルの絶対パス
     */
    public static double[][][] readSignalFromImage(String filePath) {
        InputStream is = null;


        // ステップ1: まずクラスパス上のリソースとして読み込みを試みる
        is = FileIO.class.getResourceAsStream(filePath);

        // ステップ2: クラスパスで見つからなかった場合(isがnullの場合)、ファイルシステムのパスとして試みる
        if (is == null) {
            try {
                is = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                // クラスパスにもファイルシステムにも存在しなかった場合
                System.err.println("指定されたパスはリソースとしてもファイルとしても見つかりませんでした: " + filePath);
                return null;
            }
        }

        // ステップ3: InputStreamが取得できたら、共通の画像読み込み処理を実行する
        try (InputStream autoCloseableIs = is) {
            BufferedImage image = ImageIO.read(autoCloseableIs);
            if (image == null) {
                System.err.println("画像の読み込みに失敗しました(サポートされていない形式の可能性があります): " + filePath);
                return null;
            }
            int width = image.getWidth();
            int height = image.getHeight();
            double[][][] result = new double[width][height][3];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    result[x][y][0] = (double) ((rgb >> 16) & 0xFF);
                    result[x][y][1] = (double) ((rgb >> 8) & 0xFF);
                    result[x][y][2] = (double) (rgb & 0xFF);
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 1次元データを保存
     */
    public static void writeSignalToCSV (double[] signalData, String filePath) { //ファイルの保存先の指定はメニューが行う予定
        File csvFile = new File(filePath);
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
    public static void writeSignalToImage (double[][][] imageData, String filePath) {
        File imageFile = new File(filePath);
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
    