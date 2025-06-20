package Fourier; // Fourierパッケージに属するMenuクラスを定義

import Fourier.controller.FourierController; // FourierControllerクラスをインポート
import javax.swing.*; // GUI部品を使えるようにする
import java.awt.*; // グラフィックやイベントを扱えるようにする
import java.util.stream.Stream; // Streamを使えるようにする

public class Menu {
    private static JFrame frame; // JFrameを使ってウィンドウを作成

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
     * Indataがtrueかつ入力されたデータが1次元の場合、readSignalFromCSVメソッドを呼び出して1次元の入力信号を読み込む。
     * Indataがtrueかつ入力されたデータが2次元の場合、readSignalFromImageメソッドを呼び出して2次元の入力信号を読み込む。
     * Keepdataがtrueかつ保存するデータが1次元の場合、writeSignalToCSVメソッドを呼び出して1次元の信号データを保存する。
     * Keepdataがtrueかつ保存するデータが2次元の場合、writeSignalToImageメソッドを呼び出して2次元の信号データを保存する。
     * このメソッドは、ストリームを使用して条件に合致する場合のみ処理を実行する。
     */

    public void callFileIO() {
        Stream.of(FourierController.Indata) // Indataがtrueまたはfalse
                .filter(aBoolean -> aBoolean && Dimensional == 0) // trueかつデータが1次元のときだけ通す
                .forEach(aBoolean -> FileIO.readSignalFromCSV(getOpenFilePath())); // readSignalFromCSVを実行

        Stream.of(FourierController.Indata) // Indataがtrueまたはfalse
                .filter(aBoolean -> aBoolean && Dimensional == 1) // trueかつデータが2次元のときだけ通す
                .forEach(aBoolean -> FileIO.readSignalFromImage(getOpenFilePath())); // readSignalFromImageを実行

        Stream.of(FourierController.Keepdata) // Keepdataがtrueまたはfalse
                .filter(aBoolean -> aBoolean && Dimensional == 0) // trueかつデータが1次元のときだけ通す
                .forEach(aBoolean -> {
                    FileIO.writeSignalToCSV(getSaveFilePath());
                }); // writeSignalToCSVを実行

        Stream.of(FourierController.Keepdata) // Keepdataがtrueまたはfalse
                .filter(aBoolean -> aBoolean && Dimensional == 1) // trueかつデータが2次元のときだけ通す
                .forEach(aBoolean -> {
                    FileIO.writeSignalToImage(getSaveFilePath());
                }); // writeSignalToImageを実行
    }

    /**
     * ファイル選択ダイアログを表示し、ユーザが選択したファイルのパスを取得するメソッド
     * このメソッドは、JFileChooserを使用してファイル選択ダイアログを表示し、
     * ユーザが選択したファイルのパスを返す。
     * ユーザがファイルを選択しなかった場合はnullを返す。
     */

    public static String getOpenFilePath() {
        JFileChooser openfileChooser = new JFileChooser(); // ファイル選択ダイアログを作成
        int decide = openfileChooser.showOpenDialog(frame); // ダイアログを表示し、ユーザの選択を待つ

        return Stream.of(decide)
                .filter(d -> d == JFileChooser.APPROVE_OPTION) // ユーザがファイルを選択した場合のみ通す
                .map(d -> openfileChooser.getSelectedFile().getAbsolutePath()) // 選択されたファイルのパスを取得
                .findFirst() // 最初の要素を取得
                .orElse(null); // ファイルが選択されなかった場合はnullを返す
    }

    /**
     * ファイル保存ダイアログを表示し、ユーザが保存するファイルのパスを取得するメソッド
     * このメソッドは、JFileChooserを使用してファイル保存ダイアログを表示し、
     * ユーザが保存するファイルのパスを返す。
     * ユーザがファイルを保存しなかった場合はnullを返す。
     */

    public static String getSaveFilePath() {
        JFileChooser savefileChooser = new JFileChooser(); // ファイル保存ダイアログを作成
        int decide = savefileChooser.showSaveDialog(frame); // ダイアログを表示し、ユーザの選択を待つ

        return Stream.of(decide)
                .filter(d -> d == JFileChooser.APPROVE_OPTION) // ユーザがファイルを選択した場合のみ通す
                .map(d -> savefileChooser.getSelectedFile().getAbsolutePath()) // 選択されたファイルのパスを取得
                .findFirst() // 最初の要素を取得
                .orElse(null); // ファイルが選択されなかった場合はnullを返す
    }
}
