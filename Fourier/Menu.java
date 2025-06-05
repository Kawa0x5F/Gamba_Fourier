package Fourier;

import Fourier.controller.FourierController; // FourierControllerクラスをインポート
import Fourier.FileIO; // FileIOクラスをインポート
import javax.swing.*; // GUI部品を使えるようにする
import java.awt.*; // グラフィックやイベントを扱えるようにする
import java.awt.event.MouseAdapter; // マウスイベントを扱う
import java.awt.event.MouseEvent; // マウスイベント情報を保持する
import java.util.stream.IntStream; // IntStreamを使えるようにする
import java.util.stream.Stream; // Streamを使えるようにする

public class Menu {
    
    public void displayMenuScreen() {

        // マウスの現在位置を取得
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

        // メニュー用のウィンドウを作成
        JFrame menuFrame = new JFrame("Menu"); // 「Menu」というタイトルのウィンドウを作る
        menuFrame.setSize(110, 150); // ウィンドウの大きさを幅100ピクセル、高さ200ピクセルに設定
        menuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ウィンドウを閉じたら、そのウィンドウだけを消す
        menuFrame.setUndecorated(true); // ウィンドウのタイトルバーや枠を非表示にする
        menuFrame.setLocation(mouseLocation); // 先ほど取得したマウスの位置にウィンドウを配置する

        // メインパネル（縦に4分割）
        JPanel panel = new JPanel(new GridLayout(3, 1));

        String[] labels = { "Date Entry", "Date Strage", "Spectrum Reset" }; // セクションに追加するテキストの要素

        // Stream API を用いてセクション作成＆追加
        IntStream.range(0, 3).mapToObj(i -> {
            final JPanel section = new JPanel(new BorderLayout()); // セクションパネルを作成（中央にラベル配置のため BorderLayout）
            final JLabel label = new JLabel(String.valueOf(labels[i]), SwingConstants.CENTER); // ラベルを作成（中央寄せ）
            section.add(label, BorderLayout.CENTER); // ラベルをセクションに追加

            final Color originalColor = Color.WHITE; // 初期背景色
            section.setBackground(originalColor); // 背景色を設定
            section.setPreferredSize(new Dimension(110, 50)); // サイズを設定

            section.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    section.setBackground(Color.GRAY); // ホバー時、背景色をグレーに設定
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    section.setBackground(originalColor); // 元の色に戻す
                }

                // boolean変数を反転させる(true,false)
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (i) {
                        case 0:
                            FourierController.Indata = !FourierController.Indata;
                            System.out.println("Indata toggled: " + FourierController.Indata); // 反転しているかの確認
                            break;
                        case 1:
                            FourierController.Keepdate = !FourierController.Keepdate;
                            System.out.println("Keepdate toggled: " + FourierController.Keepdate); // 反転しているかの確認
                            break;
                        case 2:
                            FourierController.Respectrum = !FourierController.Respectrum;
                            System.out.println("Respectrum toggled: " + FourierController.Respectrum); // 反転しているかの確認
                            break;
                    }
                }
            });

            return section; // セクションを返す
        }).forEach(panel::add); // パネルに追加

        menuFrame.add(panel);
        menuFrame.setVisible(true);
    }

    public void callFileIO() {

        Stream.of(FourierController.Indata) // Indataがtrueまたはfalse
                .filter(aBoolean -> aBoolean) // trueのときだけ通す
                .forEach(aBoolean -> FileIO.input1dDiscreteSignal()); // input1dDiscreteSignalを実行

        Stream.of(FourierController.Keepdate) // Keepdateがtrueまたはfalse
                .filter(aBoolean -> aBoolean) // trueのときだけ通す
                .forEach(aBoolean -> {
                    FileIO.output1dDiscreteSignal();
                    FileIO.outputOperation1dPowerSpectrum();
                    FileIO.output1dRestorationDiscreteSignal();
                }); // output1dDiscreteSignal, outputOperation1dPowerSpectrum,
                    // output1dRestorationDiscreteSignalを実行
    }
}
