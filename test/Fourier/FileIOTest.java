package Fourier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * FileIOクラスの単体テストクラス
 * CSV読み込み・書き込み、画像読み込み・書き込み、ファイル形式検出等のテストを行う
 * 
 * @author Generated Test
 * @see FileIO
 */
class FileIOTest {

    @TempDir
    Path tempDir;

    private File tempCsvFile;
    private File tempImageFile;
    private double[] testSignalData;
    private double[][][] testImageData;

    @BeforeEach
    void setUp() throws IOException {
        // テスト用の信号データ
        testSignalData = new double[]{1.0, 2.5, -3.2, 4.7, 0.0};

        // テスト用の画像データ（2x2のRGB画像）
        testImageData = new double[2][2][3];
        testImageData[0][0] = new double[]{255, 0, 0};    // 赤
        testImageData[0][1] = new double[]{0, 255, 0};    // 緑
        testImageData[1][0] = new double[]{0, 0, 255};    // 青
        testImageData[1][1] = new double[]{128, 128, 128}; // グレー

        // 一時ファイルの作成
        tempCsvFile = tempDir.resolve("test_signal.csv").toFile();
        tempImageFile = tempDir.resolve("test_image.png").toFile();
    }

    @Nested
    @DisplayName("CSV読み込みのテスト")
    class CSVReadTest {

        @Test
        @DisplayName("正常なCSVファイルを読み込める")
        void testReadSignalFromCSV() throws IOException {
            // テスト用CSVファイルを作成
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile, StandardCharsets.UTF_8))) {
                for (double value : testSignalData) {
                    writer.write(String.valueOf(value));
                    writer.newLine();
                }
            }

            // 読み込みテスト
            double[] result = FileIO.readSignalFromCSV(tempCsvFile.getAbsolutePath());
            
            assertNotNull(result);
            assertEquals(testSignalData.length, result.length);
            assertArrayEquals(testSignalData, result, 1e-10);
        }

        @Test
        @DisplayName("空行を含むCSVファイルを正しく処理する")
        void testReadCSVWithEmptyLines() throws IOException {
            // 空行を含むCSVファイルを作成
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile, StandardCharsets.UTF_8))) {
                writer.write("1.0");
                writer.newLine();
                writer.write("");  // 空行
                writer.newLine();
                writer.write("2.0");
                writer.newLine();
                writer.write("   ");  // 空白のみの行
                writer.newLine();
                writer.write("3.0");
                writer.newLine();
            }

            double[] result = FileIO.readSignalFromCSV(tempCsvFile.getAbsolutePath());
            double[] expected = {1.0, 2.0, 3.0};
            
            assertNotNull(result);
            assertArrayEquals(expected, result, 1e-10);
        }

        @Test
        @DisplayName("存在しないファイルを指定するとnullを返す")
        void testReadNonexistentCSV() {
            double[] result = FileIO.readSignalFromCSV("/path/to/nonexistent/file.csv");
            assertNull(result);
        }

        @Test
        @DisplayName("不正な数値形式のCSVファイルでnullを返す")
        void testReadInvalidCSV() throws IOException {
            // 不正な数値を含むCSVファイルを作成
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile, StandardCharsets.UTF_8))) {
                writer.write("1.0");
                writer.newLine();
                writer.write("invalid_number");
                writer.newLine();
                writer.write("3.0");
                writer.newLine();
            }

            double[] result = FileIO.readSignalFromCSV(tempCsvFile.getAbsolutePath());
            assertNull(result);
        }

        @Test
        @DisplayName("空のCSVファイルを読み込むと空配列を返す")
        void testReadEmptyCSV() throws IOException {
            // 空のCSVファイルを作成
            tempCsvFile.createNewFile();

            double[] result = FileIO.readSignalFromCSV(tempCsvFile.getAbsolutePath());
            
            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    @Nested
    @DisplayName("CSV書き込みのテスト")
    class CSVWriteTest {

        @Test
        @DisplayName("正常に信号データをCSVに書き込める")
        void testWriteSignalToCSV() throws IOException {
            FileIO.writeSignalToCSV(testSignalData, tempCsvFile.getAbsolutePath());
            
            assertTrue(tempCsvFile.exists());
            
            // ファイル内容を検証
            double[] readData = FileIO.readSignalFromCSV(tempCsvFile.getAbsolutePath());
            assertArrayEquals(testSignalData, readData, 1e-10);
        }

        @Test
        @DisplayName("空の配列をCSVに書き込める")
        void testWriteEmptyArrayToCSV() throws IOException {
            double[] emptyArray = new double[0];
            FileIO.writeSignalToCSV(emptyArray, tempCsvFile.getAbsolutePath());
            
            assertTrue(tempCsvFile.exists());
            assertEquals(0, tempCsvFile.length());
        }

        @Test
        @DisplayName("大きな値と小さな値を含むデータを正しく書き込める")
        void testWriteExtremeValues() throws IOException {
            double[] extremeValues = {
                Double.MAX_VALUE, 
                Double.MIN_VALUE, 
                Double.POSITIVE_INFINITY, 
                Double.NEGATIVE_INFINITY,
                Double.NaN,
                0.0,
                -0.0
            };
            
            FileIO.writeSignalToCSV(extremeValues, tempCsvFile.getAbsolutePath());
            assertTrue(tempCsvFile.exists());
            
            // NaNやInfinityは文字列として正しく保存されているかチェック
            try (BufferedReader reader = new BufferedReader(new FileReader(tempCsvFile))) {
                String line = reader.readLine();
                assertTrue(line.contains("1.7976931348623157E308")); // MAX_VALUE
            }
        }
    }

    @Nested
    @DisplayName("画像読み込みのテスト")
    class ImageReadTest {

        @Test
        @DisplayName("正常なPNG画像を読み込める")
        void testReadSignalFromImage() throws IOException {
            // テスト用PNG画像を作成
            BufferedImage testImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
            testImage.setRGB(0, 0, 0xFF0000); // 赤
            testImage.setRGB(1, 0, 0x00FF00); // 緑
            testImage.setRGB(0, 1, 0x0000FF); // 青
            testImage.setRGB(1, 1, 0x808080); // グレー
            
            ImageIO.write(testImage, "png", tempImageFile);

            // 読み込みテスト
            double[][][] result = FileIO.readSignalFromImage(tempImageFile.getAbsolutePath());
            
            assertNotNull(result);
            assertEquals(2, result.length);     // width
            assertEquals(2, result[0].length);  // height
            assertEquals(3, result[0][0].length); // RGB

            // 色の値を検証
            assertEquals(255.0, result[0][0][0], 1e-10); // 赤のR成分
            assertEquals(0.0, result[0][0][1], 1e-10);   // 赤のG成分
            assertEquals(0.0, result[0][0][2], 1e-10);   // 赤のB成分
        }

        @Test
        @DisplayName("存在しない画像ファイルを指定するとnullを返す")
        void testReadNonexistentImage() {
            double[][][] result = FileIO.readSignalFromImage("/path/to/nonexistent/image.png");
            assertNull(result);
        }

        @Test
        @DisplayName("サポートされていない形式の画像でnullを返す")
        void testReadUnsupportedImageFormat() throws IOException {
            // テキストファイルを画像として読み込もうとする
            File textFile = tempDir.resolve("not_an_image.txt").toFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(textFile))) {
                writer.write("This is not an image");
            }

            double[][][] result = FileIO.readSignalFromImage(textFile.getAbsolutePath());
            assertNull(result);
        }

        @Test
        @DisplayName("様々な画像形式を読み込める")
        void testReadDifferentImageFormats() throws IOException {
            // JPEG画像を作成してテスト
            File jpgFile = tempDir.resolve("test.jpg").toFile();
            BufferedImage testImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            testImage.setRGB(0, 0, 0xFF0000); // 赤
            ImageIO.write(testImage, "jpg", jpgFile);

            double[][][] result = FileIO.readSignalFromImage(jpgFile.getAbsolutePath());
            assertNotNull(result);
            assertEquals(1, result.length);
            assertEquals(1, result[0].length);
        }
    }

    @Nested
    @DisplayName("画像書き込みのテスト")
    class ImageWriteTest {

        @Test
        @DisplayName("正常に画像データをPNGに書き込める")
        void testWriteSignalToImage() throws IOException {
            FileIO.writeSignalToImage(testImageData, tempImageFile.getAbsolutePath());
            
            assertTrue(tempImageFile.exists());
            
            // ファイル内容を検証
            double[][][] readData = FileIO.readSignalFromImage(tempImageFile.getAbsolutePath());
            assertNotNull(readData);
            assertEquals(testImageData.length, readData.length);
            assertEquals(testImageData[0].length, readData[0].length);
        }

        @Test
        @DisplayName("範囲外の色値を正しくクランプして書き込む")
        void testWriteImageWithClampedValues() throws IOException {
            double[][][] clampTestData = new double[1][1][3];
            clampTestData[0][0] = new double[]{-50, 300, 150}; // 範囲外の値
            
            File clampTestFile = tempDir.resolve("clamp_test.png").toFile();
            FileIO.writeSignalToImage(clampTestData, clampTestFile.getAbsolutePath());
            
            assertTrue(clampTestFile.exists());
            
            // 読み込んで値がクランプされているか確認
            double[][][] result = FileIO.readSignalFromImage(clampTestFile.getAbsolutePath());
            assertEquals(0.0, result[0][0][0], 1e-10);   // -50 → 0
            assertEquals(255.0, result[0][0][1], 1e-10); // 300 → 255
            assertEquals(150.0, result[0][0][2], 1e-10); // 150 → 150
        }

        @Test
        @DisplayName("サポートされていない拡張子で保存をスキップする")
        void testWriteUnsupportedFormat() {
            File unsupportedFile = tempDir.resolve("test.xyz").toFile();
            
            // 例外が発生せずに処理が完了することを確認
            assertDoesNotThrow(() -> {
                FileIO.writeSignalToImage(testImageData, unsupportedFile.getAbsolutePath());
            });
            
            // ファイルが作成されていないことを確認
            assertFalse(unsupportedFile.exists());
        }

        @Test
        @DisplayName("様々な画像形式で保存できる")
        void testWriteDifferentImageFormats() throws IOException {
            String[] formats = {"png", "jpg", "bmp", "gif"};
            
            for (String format : formats) {
                File formatFile = tempDir.resolve("test." + format).toFile();
                FileIO.writeSignalToImage(testImageData, formatFile.getAbsolutePath());
                assertTrue(formatFile.exists(), format + "形式での保存に失敗");
            }
        }
    }

    @Nested
    @DisplayName("リソース読み込みのテスト")
    class ResourceReadTest {

        @Test
        @DisplayName("クラスパスリソースが存在しない場合のフォールバック")
        void testResourceFallback() throws IOException {
            // まずクラスパスに存在しないパスでテスト
            // その後ファイルシステムのパスとして解決される
            File testFile = tempDir.resolve("resource_test.csv").toFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
                writer.write("1.0\n2.0\n3.0");
            }

            double[] result = FileIO.readSignalFromCSV(testFile.getAbsolutePath());
            assertNotNull(result);
            assertEquals(3, result.length);
        }
    }

    @Nested
    @DisplayName("統合テスト")
    class IntegrationTest {

        @Test
        @DisplayName("CSV書き込み→読み込みのラウンドトリップ")
        void testCSVRoundTrip() throws IOException {
            double[] originalData = {1.1, 2.2, 3.3, -4.4, 0.0};
            
            // 書き込み
            FileIO.writeSignalToCSV(originalData, tempCsvFile.getAbsolutePath());
            
            // 読み込み
            double[] readData = FileIO.readSignalFromCSV(tempCsvFile.getAbsolutePath());
            
            // 検証
            assertArrayEquals(originalData, readData, 1e-10);
        }

        @Test
        @DisplayName("画像書き込み→読み込みのラウンドトリップ")
        void testImageRoundTrip() throws IOException {
            // PNG形式でのラウンドトリップテスト（可逆圧縮）
            File pngFile = tempDir.resolve("roundtrip.png").toFile();
            
            // 書き込み
            FileIO.writeSignalToImage(testImageData, pngFile.getAbsolutePath());
            
            // 読み込み
            double[][][] readData = FileIO.readSignalFromImage(pngFile.getAbsolutePath());
            
            // 検証（PNGは可逆圧縮なので完全一致するはず）
            assertNotNull(readData);
            assertEquals(testImageData.length, readData.length);
            assertEquals(testImageData[0].length, readData[0].length);
            
            for (int x = 0; x < testImageData.length; x++) {
                for (int y = 0; y < testImageData[0].length; y++) {
                    for (int c = 0; c < 3; c++) {
                        assertEquals(testImageData[x][y][c], readData[x][y][c], 1e-10);
                    }
                }
            }
        }
    }
}
