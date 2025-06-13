package Fourier; // Fourierパッケージに属するMenuクラスを定義

import Fourier.controller.FourierController; // FourierControllerクラスをインポート
import javax.swing.*; // GUI部品を使えるようにする
import java.awt.*; // グラフィックやイベントを扱えるようにする
import java.util.stream.Stream; // Streamを使えるようにする

public class Menu {

    /**
     * メニューを表示するためのメソッドを呼び出す
     * このメソッドは、マウスカーソルの位置にポップアップメニューを表示し、
     * 「data Entry」や「data Storage」などのメニュー項目を提供する。
     * 各メニュー項目は、クリックされたときに特定のアクションを実行する。
     * 例えば、「data Entry」をクリックすると、FourierController.Indataの値が反転し、
     * 現在の値がコンソールに表示される。また、callFileIOメソッドが呼び出され、
     * データの読み込みが行われる。
     */

    public void displayMenuScreen() {
        // 現在のマウスカーソルの位置を取得する
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

        // メニューを作成
        JPopupMenu popupMenu = new JPopupMenu();

        // 「Date Entry」メニュー項目
        JMenuItem entryData = new JMenuItem("data Entry");
        // クリックされたときIndataの値を反転
        entryData.addActionListener(e -> {
            FourierController.Indata = !FourierController.Indata; // 反転させる
            System.out.println("Indata toggled: " + FourierController.Indata); // 現在の値を表示
            callFileIO(); // callFileIOメソッドを呼び出す
        });
        // ポップアップメニューにこの項目を追加
        popupMenu.add(entryData);

        // 「Date Storage」メニュー項目
        JMenuItem storageData = new JMenuItem("data Storage");
        // クリックされたときKeepdataの値を反転
        storageData.addActionListener(e -> {
            FourierController.Keepdata = !FourierController.Keepdata; // 反転させる
            System.out.println("Keepdata toggled: " + FourierController.Keepdata); // 現在の値を表示
            callFileIO(); // callFileIOメソッドを呼び出す
        });
        // ポップアップメニューにこの項目を追加
        popupMenu.add(storageData);

        // //「Spectrum Reset」メニュー項目
        // JMenuItem resetSpectrum = new JMenuItem("Spectrum Reset");
        // // クリックされたときRespectrumの値を反転
        // resetSpectrum.addActionListener(e -> {
        // FourierController.Respectrum = !FourierController.Respectrum; // 反転させる
        // System.out.println("Respectrum toggled: " + FourierController.Respectrum); //
        // 現在の値を表示
        // callFileIO(); // callFileIOメソッドを呼び出す
        // });
        // // ポップアップメニューにこの項目を追加
        // popupMenu.add(resetSpectrum);

        // メニューを表示するためのウィンドウを用意
        JFrame frame = new JFrame(); // 枠なしのウィンドウを新しく作成
        frame.setUndecorated(true); // タイトルバーや閉じるボタンなどを非表示にする
        frame.setSize(0, 0); // ウィンドウ自体は最小に設定
        frame.setLocation(mouseLocation); // ウィンドウをマウス位置に移動
        frame.setVisible(true); // ウィンドウを表示

        // メニューを表示
        SwingUtilities.invokeLater(() -> {
            // フレーム上の (0,0) の位置にメニューを表示
            popupMenu.show(frame, 0, 0);
        });
    }

    /**
     * ファイル入出力を呼び出すメソッド
     * このメソッドは、FourierControllerのIndataとKeepdataの値に基づいて、
     * 入力データの読み込みを行う。
     * Indataがtrueの場合、input1dDiscreteSignalメソッドを呼び出して入力信号を読み込む。
     * Keepdataがtrueの場合、output1dDiscreteSignal、outputOperation1dPowerSpectrum、
     * output1dRestorationDiscreteSignalメソッドを呼び出して信号データを保存する。
     * このメソッドは、ストリームを使用して条件に合致する場合のみ処理を実行する。
     */

    public void callFileIO() {
        Stream.of(FourierController.Indata) // Indataがtrueまたはfalse
                .filter(aBoolean -> aBoolean) // trueのときだけ通す
                .forEach(aBoolean -> FileIO.input1dDiscreteSignal()); // input1dDiscreteSignalを実行

        Stream.of(FourierController.Keepdata) // Keepdataがtrueまたはfalse
                .filter(aBoolean -> aBoolean) // trueのときだけ通す
                .forEach(aBoolean -> {
                    FileIO.output1dDiscreteSignal();
                    FileIO.outputOperation1dPowerSpectrum();
                    FileIO.output1dRestorationDiscreteSignal();
                }); // output1dDiscreteSignal, outputOperation1dPowerSpectrum,
                    // output1dRestorationDiscreteSignalを実行
    }
}
