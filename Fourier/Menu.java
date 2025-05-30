package Fourier;

import Fourier.view.FourierView;

public class Menu extends FourierView {


    public void displayMenuScreen() {

        FourierView.PutWindow(); // メニュー画面表示

    }

    public void callFileIO() {

        FileIO.input1dDiscreteSignal(); // １次元元信号の入力
        FileIO.output1dDiscreteSignal(); // １次元パワースペクトルの出力
        FileIO.outputOperation1dPowerSpectrum(); // 操作後１次元パワースペクトルの出力
        FileIO.output1dRestorationDiscreteSignal(); // １次元復元信号の出力

    }

    public void callMouseListener() {

    }

}
