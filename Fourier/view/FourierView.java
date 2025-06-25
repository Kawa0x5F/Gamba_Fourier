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
    // *** 変更点 *** JPanelを直接保持するように変更
    protected final Map<String, JPanel> panels;

    public FourierView(FourierModel model, String title) {
        this.model = model;
        this.model.addPropertyChangeListener(this);

        this.frame = new JFrame(title);
        // *** 変更点 *** JPanelを直接保持するように変更
        this.panels = new HashMap<>();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    protected FourierModel getModel() {
        return this.model;
    }
    
    // *** 変更点 *** JPanelを直接受け取るように変更
    protected void addPanel(String name, JPanel panel) {
        this.panels.put(name, panel);
        this.frame.add(panel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        SwingUtilities.invokeLater(this::updateView);
    }
    
    public void setVisible(boolean visible) {
        this.frame.setVisible(visible);
    }

    // *** 変更点 *** ここから下のリスナー登録メソッドを修正

    /**
     * 指定されたキーを持つパネルにマウスリスナーを登録します。
     * @param panelKey パネルを識別するキー（タイトル文字列）
     * @param listener 登録するマウスリスナー
     */
    public void addMouseListenerToPanel(String panelKey, MouseListener listener) {
        JPanel panel = this.panels.get(panelKey);
        if (panel != null) {
            panel.addMouseListener(listener);
        } else {
            System.err.println("Warning: Panel with key '" + panelKey + "' not found.");
        }
    }

    /**
     * 指定されたキーを持つパネルにマウスモーションリスナーを登録します。
     * @param panelKey パネルを識別するキー（タイトル文字列）
     * @param listener 登録するマウスモーションリスナー
     */
    public void addMouseMotionListenerToPanel(String panelKey, MouseMotionListener listener) {
        JPanel panel = this.panels.get(panelKey);
        if (panel != null) {
            panel.addMouseMotionListener(listener);
        } else {
            System.err.println("Warning: Panel with key '" + panelKey + "' not found.");
        }
    }

    protected abstract void updateView();
}