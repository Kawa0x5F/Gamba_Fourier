package Fourier.view;

import Fourier.model.FourierModel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * フーリエ変換ビューの抽象基底クラス。
 * 1次元と2次元のフーリエ変換ビューで共通する機能を提供します。
 */
public abstract class FourierView implements PropertyChangeListener {
    private final FourierModel model;
    protected final JFrame frame;
    protected final Map<String, JPanel> panels;
    
    private final JPanel contentPanel;

    /**
     * フーリエビューを作成します。
     * @param model フーリエ変換モデル
     * @param title ウィンドウタイトル
     */
    public FourierView(FourierModel model, String title) {
        this.model = model;
        this.model.addPropertyChangeListener(this);
        this.frame = new JFrame(title);
        this.panels = new HashMap<>();

        // フレームのレイアウトをBorderLayoutに設定
        this.frame.setLayout(new BorderLayout(5, 5));
        
        // メインコンテンツ用のパネルをGridLayoutで初期化
        this.contentPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        this.frame.add(contentPanel, BorderLayout.CENTER);

        // ブラシ調整用のコントロールパネルを作成して追加
        JPanel controlPanel = createBrushControlPanel();
        this.frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    /**
     * モデルを取得します。
     * @return フーリエ変換モデル
     */
    public FourierModel getModel() {
        return this.model;
    }
    
    /**
     * フレームを取得します。
     * @return JFrameオブジェクト
     */
    public JFrame getFrame() {
        return this.frame;
    }
    
    /**
     * パネルをコンテンツパネルに追加します。
     * @param name パネル名
     * @param panel 追加するパネル
     */
    protected void addPanel(String name, JPanel panel) {
        this.panels.put(name, panel);
        this.contentPanel.add(panel);
    }
    
    /**
     * ブラシサイズ調整用UIパネルを生成するヘルパーメソッド
     */
    private JPanel createBrushControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        int initialBrushSize = getModel().getBrushSize();
        
        JSlider brushSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, initialBrushSize);
        brushSlider.setMajorTickSpacing(1);
        brushSlider.setPaintTicks(true);
        brushSlider.setPaintLabels(true);
        
        brushSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) { 
                getModel().setBrushSize(source.getValue());
            }
        });
        
        controlPanel.add(new JLabel("Brush Size:"));
        controlPanel.add(brushSlider);
        
        return controlPanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        SwingUtilities.invokeLater(this::updateView);
    }
    
    public void setVisible(boolean visible) {
        this.frame.setVisible(visible);
    }

    public void addMouseListenerToPanel(String panelKey, MouseListener listener) {
        JPanel panel = this.panels.get(panelKey);
        if (panel != null) {
            panel.addMouseListener(listener);
        } else {
            System.err.println("Warning: Panel with key '" + panelKey + "' not found.");
        }
    }

    public void addMouseMotionListenerToPanel(String panelKey, MouseMotionListener listener) {
        JPanel panel = this.panels.get(panelKey);
        if (panel != null) {
            panel.addMouseMotionListener(listener);
        } else {
            System.err.println("Warning: Panel with key '" + panelKey + "' not found.");
        }
    }

    public void addMouseListenerToAllPanels(MouseListener listener) {
        for (JPanel panel : this.panels.values()) {
            panel.addMouseListener(listener);
        }
    }

    protected abstract void updateView();
}