package Fourier.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import Fourier.Complex;
import Fourier.FFTUtil;

/**
 * FourierModel1DクラスのFFTおよびIFFTの機能をテストするクラスです。
 * * @see FourierModel1D
 */
class FourierModel1DTest {

    private FourierModel1D fourierModel;
    // 浮動小数点数の比較に使用する許容誤差
    private static final double DELTA = 1e-9;

    /**
     * 各テストの実行前に、新しいFourierModel1Dインスタンスを初期化します。
     */
    @BeforeEach
    void setUp() {
        fourierModel = new FourierModel1D();
    }

    /**
     * FFT（高速フーリエ変換）の正確性をテストします。
     * <p>
     * N=4の既知の入力データに対してFFTを実行し、理論値と比較します。
     * 入力: [1, 2, 3, 4]
     * 理論的なFFT結果: [10, -2+2i, -2, -2-2i]
     * </p>
     * <p>
     * 注: FFTUtilのstaticメソッドを直接使用してテストします。
     * </p>
     */
    @Test
    @DisplayName("FFTが正しく計算されるかテスト")
    void testFFT() {
        // 1. セットアップ: テストデータ準備
        Complex[] inputData = {
            new Complex(1, 0),
            new Complex(2, 0),
            new Complex(3, 0),
            new Complex(4, 0)
        };

        // 期待される結果 (理論値)
        Complex[] expectedOutput = {
            new Complex(10, 0),
            new Complex(-2, 2),
            new Complex(-2, 0),
            new Complex(-2, -2)
        };

        // 2. 実行: FFTを計算
        // inputDataをコピーしてFFTを実行
        Complex[] testData = inputData.clone();
        FFTUtil.fft(testData, 0, testData.length);
        FFTUtil.bitReverseReorder(testData);

        // 3. 検証: 結果が期待値と一致するか確認
        assertEquals(expectedOutput.length, testData.length, "配列の長さが一致しません");
        for (int i = 0; i < expectedOutput.length; i++) {
            assertEquals(expectedOutput[i].getReal(), testData[i].getReal(), DELTA, "Index " + i + " の実数部が一致しません");
            assertEquals(expectedOutput[i].getImaginary(), testData[i].getImaginary(), DELTA, "Index " + i + " の虚数部が一致しません");
        }
    }

    /**
     * IFFT（逆高速フーリエ変換）の正確性をテストします。
     * <p>
     * あるデータに対してFFTを実行し、その結果にIFFTを適用すると、
     * 元のデータ（スケーリング誤差を除く）に戻ることを確認します。
     * IFFT(FFT(data)) == data
     * </p>
     */
    @Test
    @DisplayName("IFFTがFFTの結果を元のデータに戻せるかテスト")
    void testIFFT() {
        // 1. セットアップ: テストデータ準備
        Complex[] originalData = {
            new Complex(1, 0),
            new Complex(2, 0),
            new Complex(3, 0),
            new Complex(4, 0),
            new Complex(5, 0),
            new Complex(6, 0),
            new Complex(7, 0),
            new Complex(8, 0)
        };

        // 2. 実行: FFT -> IFFT
        // まず順変換 (FFT)
        Complex[] testData = originalData.clone();
        FFTUtil.fft(testData, 0, testData.length);
        FFTUtil.bitReverseReorder(testData);
        
        // 次に逆変換 (IFFT)
        FFTUtil.ifft(testData);

        // 3. 検証: 逆変換後のデータが元のデータと一致するか確認
        assertEquals(originalData.length, testData.length, "配列の長さが一致しません");
        for (int i = 0; i < originalData.length; i++) {
            assertEquals(originalData[i].getReal(), testData[i].getReal(), DELTA, "Index " + i + " の実数部が一致しません");
            assertEquals(originalData[i].getImaginary(), testData[i].getImaginary(), DELTA, "Index " + i + " の虚数部が一致しません");
        }
    }
    
    /**
     * FourierModel1Dのコンストラクタとセッターをテストします。
     */
    @Test
    @DisplayName("FourierModel1Dの初期化テスト")
    void testInitialization() {
        // デフォルトコンストラクタでモデルが初期化されることを確認
        assertNotNull(fourierModel);
        
        // 初期データでコンストラクタを使用するテスト
        double[] initialData = {1.0, 2.0, 3.0, 4.0};
        FourierModel1D modelWithData = new FourierModel1D(initialData);
        
        assertNotNull(modelWithData);
        assertArrayEquals(initialData, modelWithData.getInitialOriginData(), DELTA);
    }
    
    /**
     * マウスポイントからの計算をテストします。
     */
    @Test
    @DisplayName("マウスポイントからの計算テスト")
    void testComputeFromMousePoint() {
        // 初期データを設定
        double[] initialData = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
        FourierModel1D modelWithData = new FourierModel1D(initialData);
        
        // マウスポイントでの計算をトリガー
        java.awt.Point mousePoint = new java.awt.Point(100, 100);
        modelWithData.computeFromMousePoint(mousePoint, Boolean.FALSE);
        
        // 計算ポイントが正しく設定されているか確認
        assertEquals(mousePoint, modelWithData.getLastCalculationPoint());
        assertEquals(false, modelWithData.getIsAltDown());
        
        // Alt+クリックのテスト
        modelWithData.computeFromMousePoint(mousePoint, Boolean.TRUE);
        assertEquals(true, modelWithData.getIsAltDown());
    }
}