package Fourier.view;

import Fourier.model.FourierModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public abstract class FourierView implements PropertyChangeListener {
    private final FourierModel model;
    protected final JFrame frame;
    protected final Map<String, JPanel> panels; // パネルを名前で管理

    public FourierView(FourierModel model, String title) {
        this.model = model;
        this.model.addPropertyChangeListener(this);

        this.frame = new JFrame(title);
        this.panels = new HashMap<>();

        // ウィンドウの初期設定
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    protected FourierModel getModel() {
        return this.model;
    }

    /**
     * 管理対象のパネルをウィンドウに追加する
     * @param name パネルを識別する名前
     * @param panel 追加するパネルインスタンス
     */
    protected void addPanel(String name, JPanel panel) {
        this.panels.put(name, panel);
        this.frame.add(panel); // JFrameに直接追加
    }

    /**
     * UIの更新をイベントディスパッチスレッドで安全に実行する
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        SwingUtilities.invokeLater(this::updateView);
    }
    
    /**
     * ウィンドウを表示状態にする
     */
    public void setVisible(boolean visible) {
        this.frame.setVisible(visible);
    }

    // 全てのパネルにMouseListenerを追加するメソッド
    public void addMouseListenerToAllPanels(MouseListener listener) {
        this.panels.get("User Modified Power Spectrum").addMouseListener(listener);
    }

    // 全てのパネルにMouseMotionListenerを追加するメソッド
    public void addMouseMotionListenerToAllPanels(MouseMotionListener listener) {
        this.panels.get("User Modified Power Spectrum").addMouseMotionListener(listener);
    }

    /**
     * モデルの変更をビューに反映させるための抽象メソッド
     * サブクラスで、モデルからデータを取得し、対応するパネルにセットする処理を実装する
     */
    protected abstract void updateView();
}