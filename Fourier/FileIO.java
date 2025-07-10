package Fourier;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * ファイル入出力を行うユーティリティクラス。
 * CSVファイルと画像ファイルの読み書きに対応しています。
 */
public class FileIO {

    /**
     * ファイルを自動判別して読み込みます。
     * 拡張子に基づいて、CSVファイルなら1次元データ、画像ファイルなら2次元データとして読み込みます。
     * @param filePath 読み込むファイルのリソースパス、または絶対パス
     * @return Object型の結果（double[]またはdouble[][][]）。判別できない場合はnull
     */
    public static Object readSignalFromFile(String filePath) {
        String lowerPath = filePath.toLowerCase();
        
        // 画像ファイルの場合
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") || 
            lowerPath.endsWith(".png") || lowerPath.endsWith(".bmp") || 
            lowerPath.endsWith(".gif")) {
            return readSignalFromImage(filePath);
        }
        // CSVファイルまたは不明な拡張子の場合、1次元データとして試行
        else {
            return readSignalFromCSV(filePath);
        }
    }

    /**
     * CSVファイルから1次元信号を読み込みます。
     * クラスパスリソース、ファイルシステム上の絶対パスの両方に対応します。
     * @param filePath 読み込むCSVファイルのリソースパス、または絶対パス
     * @return 読み込んだ1次元信号データ、失敗時はnull
     */
    public static double[] readSignalFromCSV(String filePath) {
        List<Double> signalList = new ArrayList<>();
        InputStream is = null;

        // ステップ1: まずクラスパス上のリソースとして読み込みを試みる
        is = FileIO.class.getResourceAsStream(filePath);

        // ステップ2: クラスパスで見つからなかった場合、ファイルシステムのパスとして試みる
        if (is == null) {
            try {
                is = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                System.err.println("指定されたパスはリソースとしてもファイルとしても見つかりませんでした: " + filePath);
                return null;
            }
        }

        // ステップ3: InputStreamが取得できたら、共通のCSV読み込み処理を実行する
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    signalList.add(Double.parseDouble(line.trim()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

        double[] result = signalList.stream().mapToDouble(Double::doubleValue).toArray();
        System.out.println("CSV読み込み完了: " + result.length + "個のデータ");
        if (result.length > 0) {
            System.out.println("最初の値: " + result[0] + ", 最後の値: " + result[result.length-1]);
        }
        return result;
    }

    /**
     * 画像ファイルから2次元信号(RGB)を読み込みます。
     * @param filePath 読み込む画像ファイルの絶対パス
     * @return RGB信号の3次元配列（[x][y][色]）、失敗時はnull
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
     * 1次元データをCSVファイルに保存します。
     * @param signalData 保存する1次元データ
     * @param filePath 保存先ファイルパス（拡張子が未指定の場合は.csvが追加される）
     */
    public static void writeSignalToCSV (double[] signalData, String filePath) { //ファイルの保存先の指定はメニューが行う予定
        if (signalData == null || signalData.length == 0) {
            System.err.println("writeSignalToCSV: データが空またはnullです。");
            return;
        }
        
        // 拡張子がない場合は.csvを追加
        if (!filePath.toLowerCase().endsWith(".csv")) {
            filePath += ".csv";
        }
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
     * 2次元画像データを画像ファイルに保存します。
     * @param imageData 保存する画像データ（[x][y][RGB]の3次元配列）
     * @param filePath 保存先ファイルパス（拡張子が未指定の場合は.pngが追加される）
     */
    public static void writeSignalToImage (double[][][] imageData, String filePath) {
        // 拡張子がない場合は.pngを追加
        if (!hasImageExtension(filePath)) {
            filePath += ".png";
        }
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
     * ファイルパスが画像の拡張子を持っているかチェックします。
     * @param filePath チェックするファイルパス
     * @return 画像の拡張子を持つ場合はtrue
     */
    private static boolean hasImageExtension(String filePath) {
        String name = filePath.toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
               name.endsWith(".png") || name.endsWith(".bmp") || 
               name.endsWith(".gif");
    }

    /**
     * 画像ファイルの拡張子を取得します。
     * 対応していない拡張子の場合はnullを返します。
     * @param imageFile 画像ファイル
     * @return 対応している拡張子名、または対応していない場合はnull
     */
    private static String getImageFormat(File imageFile) {
        String name = imageFile.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "jpg";
        else if (name.endsWith(".png")) return "png";
        else if (name.endsWith(".bmp")) return "bmp";
        else if (name.endsWith(".gif")) return "gif";
        else return null;
    } 

    /**
     * ファイルの拡張子から適切なデータ次元を判定します。
     * @param filePath ファイルパス
     * @return 1: 1次元データ（CSV等）, 2: 2次元データ（画像ファイル）, 0: 判定不可
     */
    public static int getRecommendedDimension(String filePath) {
        String lowerPath = filePath.toLowerCase();
        
        // 画像ファイルの場合は2次元
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") || 
            lowerPath.endsWith(".png") || lowerPath.endsWith(".bmp") || 
            lowerPath.endsWith(".gif")) {
            return 2;
        }
        // CSVファイルまたは不明な拡張子の場合は1次元
        else if (lowerPath.endsWith(".csv") || lowerPath.endsWith(".txt")) {
            return 1;
        }
        // その他のファイルは1次元として扱う（デフォルト）
        else {
            return 1;
        }
    }

}
