package Fourier;

import Fourier.model.FourierModel;
import Fourier.model.FourierModel1D;
import Fourier.model.FourierModel2D;
import Fourier.example.Example;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.awt.Component;

/**
 * アプリケーションの右クリックメニュー機能を提供するクラス。
 * ファイルの読み込みや、計算結果の保存を行います。
 */
public class Menu {

    private final FourierModel model;

    /**
     * メニューを作成します。
     * @param model フーリエ変換モデル
     */
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

        // メニュー項目間の区切り線
        popupMenu.addSeparator();

        // スペクトラムクリアメニュー
        JMenuItem clearItem = new JMenuItem("Clear Spectrum");
        clearItem.addActionListener(e -> {
            // モデルのインスタンスタイプをチェックして適切なメソッドを呼び出す
            if (model instanceof FourierModel1D) {
                ((FourierModel1D) model).clearUserSpectrum();
            } else if (model instanceof FourierModel2D) {
                ((FourierModel2D) model).clearUserSpectrum();
            }
        });
        popupMenu.add(clearItem);

        // スペクトラム埋め込みメニュー
        JMenuItem fillItem = new JMenuItem("Fill Spectrum");
        fillItem.addActionListener(e -> {
            // モデルのインスタンスタイプをチェックして適切なメソッドを呼び出す
            if (model instanceof FourierModel1D) {
                ((FourierModel1D) model).fillUserSpectrum();
            } else if (model instanceof FourierModel2D) {
                ((FourierModel2D) model).fillUserSpectrum();
            }
        });
        popupMenu.add(fillItem);

        popupMenu.show(invoker, x, y);
    }

    /**
     * ファイルを開く処理を実行します。
     * ファイルの拡張子に応じて、適切な次元のデモを起動します。
     * @param parent ダイアログの親コンポーネント
     */
    private void handleOpenFile(Component parent) {
        String path = getOpenFilePath(parent);
        if (path == null) return;

        // ファイルの拡張子から適切な次元を判定
        int recommendedDimension = FileIO.getRecommendedDimension(path);
        
        if (recommendedDimension == 2) {
            // 画像ファイルの場合は2次元デモを起動
            double[][][] colorData = FileIO.readSignalFromImage(path);
            if (colorData != null) {
                Example.restart2DDemoWithData(colorData);
            }
        } else {
            // CSVファイルなどの場合は1次元デモを起動
            double[] data = FileIO.readSignalFromCSV(path);
            if (data != null) {
                Example.restart1DDemoWithData(data);
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
            if (signal != null && signal.length > 0) {
                FileIO.writeSignalToCSV(signal, path);
                JOptionPane.showMessageDialog(parent, "1次元データの保存が完了しました。", "保存完了", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "保存するデータがありません。信号を処理してから保存してください。", "保存エラー", JOptionPane.WARNING_MESSAGE);
            }
        } else if (model instanceof FourierModel2D) {
            // Modelから再構成されたカラー画像(IFFT結果)を直接取得
            double[][][] colorData = ((FourierModel2D) model).getIfftResultColorData();
            if (colorData != null) {
                // 変換は不要。そのまま保存メソッドに渡す
                FileIO.writeSignalToImage(colorData, path);
                JOptionPane.showMessageDialog(parent, "画像データの保存が完了しました。", "保存完了", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * ファイル選択ダイアログを表示し、選択されたファイルのパスを取得します。
     * @param parent 親コンポーネント
     * @return ファイルパス。選択されなかった場合はnull
     */
    public static String getOpenFilePath(Component parent) {
        JFileChooser chooser = new JFileChooser();
        
        // ファイルフィルターを追加
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".csv");
            }
            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });

        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".bmp") || 
                       name.endsWith(".gif");
            }
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.png, *.bmp, *.gif)";
            }
        });
        
        // 全てのファイルフィルターを追加
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return true;
            }
            @Override
            public String getDescription() {
                return "All Files (*.*)";
            }
        });
        
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * ファイル保存ダイアログを表示し、保存するファイルのパスを取得します。
     * @param parent 親コンポーネント
     * @return ファイルパス。選択されなかった場合はnull
     */
    public static String getSaveFilePath(Component parent) {
        JFileChooser chooser = new JFileChooser();
        
        // CSVファイルフィルター
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".csv");
            }
            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });
        
        // 画像ファイルフィルター
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".bmp") || 
                       name.endsWith(".gif");
            }
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.png, *.bmp, *.gif)";
            }
        });
        
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}