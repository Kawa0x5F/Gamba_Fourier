package Fourier.view;

import Fourier.Complex;
import Fourier.model.FourierModel1D;

import java.awt.*;

public class FourierView1D extends FourierView {

    // 各パネルを識別するためのキー
    private static final String KEY_ORIGINAL = "Original Signal";
    private static final String KEY_SPECTRUM = "Power Spectrum";
    private static final String KEY_REAL = "Fourier Transform (Real)";
    private static final String KEY_IMAG = "Fourier Transform (Imaginary)";

    public FourierView1D(FourierModel1D model) {
        // 親クラスのコンストラクタを呼び出し、ウィンドウタイトルを設定
        super(model, "1D Fourier Transform");

        // ウィンドウのレイアウトを設定
        frame.setSize(800, 600);
        frame.setLayout(new GridLayout(2, 2, 5, 5)); // 2x2グリッド

        // 4つのSignalPanelを作成し、名前をつけて登録
        addPanel(KEY_ORIGINAL, new SignalPanel(KEY_ORIGINAL));
        addPanel(KEY_SPECTRUM, new SignalPanel(KEY_SPECTRUM));
        addPanel(KEY_REAL, new SignalPanel(KEY_REAL));
        addPanel(KEY_IMAG, new SignalPanel(KEY_IMAG));
        
        // 初期ビューの更新
        updateView();
        
        // ウィンドウを表示
        setVisible(true);
    }

    @Override
    protected void updateView() {
        FourierModel1D model1D = (FourierModel1D) getModel();

        // モデルからオリジナル(未処理)のデータを表示
        double[] originData = model1D.getOriginData();
        ((SignalPanel) panels.get(KEY_ORIGINAL)).setData(originData);

        // モデルからパワースペクトルを取得して表示
        double[] powerSpectrum = model1D.getCalculatedData();
        ((SignalPanel) panels.get(KEY_SPECTRUM)).setData(powerSpectrum);

        // モデルから複素数結果を取得
        Complex[] complexResult = model1D.getComplexResult();
        if (complexResult != null) {
            // 複素数配列から実部と虚部を抜き出して表示
            double[] realPart = new double[complexResult.length];
            double[] imagPart = new double[complexResult.length];
            for (int i = 0; i < complexResult.length; i++) {
                realPart[i] = complexResult[i].getReal();      // ComplexクラスにgetReal()があると仮定
                imagPart[i] = complexResult[i].getImaginary(); // ComplexクラスにgetImaginary()があると仮定
            }
            ((SignalPanel) panels.get(KEY_REAL)).setData(realPart);
            ((SignalPanel) panels.get(KEY_IMAG)).setData(imagPart);
        }
        
        // ※元信号パネル(KEY_ORIGINAL)は、このタイミングでは更新できない。
        // 元信号は、ユーザー入力など別のタイミングで描画されるか、
        // モデルが元信号を保持するよう修正する必要がある。
    }
}