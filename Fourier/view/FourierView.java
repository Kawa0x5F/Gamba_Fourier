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
    protected final Map<String, JPanel> panels;
    
    private final JPanel contentPanel;

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

    // 可視性を public に変更し、エラーの可能性をなくします
    public FourierModel getModel() {
        return this.model;
    }
    
    // パネルをフレームではなく、contentPanelに追加するように修正
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