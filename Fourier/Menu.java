package Fourier;

import Fourier.model.FourierModel;
import Fourier.model.FourierModel1D;
import Fourier.model.FourierModel2D;
import Fourier.example.Example;

import javax.swing.*;
import java.awt.*;

/**
 * アプリケーションの右クリックメニュー機能を提供するクラス。
 * ファイルの読み込みや、計算結果の保存を行います。
 */
public class Menu {

    private final FourierModel model;

    public Menu(FourierModel model) {
        this.model = model;
    }

    /**
     * 指定されたコンポーネント上の特定の位置にポップアップメニューを表示します。
     * @param invoker メニューを表示する親コンポーネント
     * @param x 表示するx座標
     * @param y 表示するy座標
     */
    public void displayMenuScreen(Component invoker, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();

        // --- ファイルを開くメニュー ---
        JMenuItem openFileItem = new JMenuItem("Open File...");
        openFileItem.addActionListener(e -> handleOpenFile(invoker));
        popupMenu.add(openFileItem);

        // --- 保存メニュー ---
        JMenuItem saveFileItem = new JMenuItem("Save Reconstructed Data As...");
        saveFileItem.addActionListener(e -> handleSave(invoker));

        // モデルの種類に応じて、保存可能なデータがあるかチェックし、なければ無効化
        if (model instanceof FourierModel1D) {
            saveFileItem.setEnabled(((FourierModel1D) model).getIfftResultData() != null);
        } else if (model instanceof FourierModel2D) {
            saveFileItem.setEnabled(((FourierModel2D) model).getIfftResultColorData() != null);
        } else {
            saveFileItem.setEnabled(false);
        }
        popupMenu.add(saveFileItem);

        popupMenu.show(invoker, x, y);
    }

    /**
     * ファイルを開く処理を実行します。
     * 選択されたファイルに応じて、1Dまたは2Dのデモを再起動します。
     * @param parent ダイアログの親コンポーネント
     */
    private void handleOpenFile(Component parent) {
        String path = getOpenFilePath(parent);
        if (path == null) return;

        if (model instanceof FourierModel1D) {
            double[] data = FileIO.readSignalFromCSV(path);
            if (data != null) {
                // Exampleクラスに新しいデータで1Dデモを再起動するように依頼
                Example.restart1DDemoWithData(data);
            }
        } else if (model instanceof FourierModel2D) {
            double[][][] colorData = FileIO.readSignalFromImage(path);
            if (colorData != null) {
                // Exampleクラスの再起動メソッドを呼び出す
                Example.restart2DDemoWithData(colorData);
            }
        }
    }

    /**
     * 再構成されたデータをファイルに保存する処理を実行します。
     * @param parent ダイアログの親コンポーネント
     */
    private void handleSave(Component parent) {
        String path = getSaveFilePath(parent);
        if (path == null) return;

        if (model instanceof FourierModel1D) {
            double[] signal = ((FourierModel1D) model).getIfftResultData();
            if (signal != null) {
                FileIO.writeSignalToCSV(signal, path);
            }
        } else if (model instanceof FourierModel2D) {
            // *************** ここから修正 ***************
            // Modelから再構成されたカラー画像(IFFT結果)を直接取得
            double[][][] colorData = ((FourierModel2D) model).getIfftResultColorData();
            if (colorData != null) {
                // 変換は不要。そのまま保存メソッドに渡す
                FileIO.writeSignalToImage(colorData, path);
            }
            // *************** ここまで修正 ***************
        }
    }

    /**
     * ファイル選択ダイアログを表示し、選択されたファイルのパスを取得します。
     * @param parent 親コンポーネント
     * @return ファイルパス。選択されなかった場合はnull。
     */
    public static String getOpenFilePath(Component parent) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * ファイル保存ダイアログを表示し、保存するファイルのパスを取得します。
     * @param parent 親コンポーネント
     * @return ファイルパス。選択されなかった場合はnull。
     */
    public static String getSaveFilePath(Component parent) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}