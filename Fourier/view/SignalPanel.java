package Fourier.view;

import javax.swing.*;
import java.awt.*;

public class SignalPanel extends JPanel {
    private double[] data;
    private int gridNumber;

    public SignalPanel(double[] data, int gridNumber) {
        this.data = data;
        this.gridNumber = gridNumber;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 描画用2Dグラフィックス
        Graphics2D g2 = (Graphics2D) g;

        // グリッド番号を左上に描画
        g2.setColor(Color.BLACK);
        g2.drawString("Grid " + gridNumber, 10, 15);

        // 信号データがなければ終了（番号のみ描画）
        if (data == null || data.length == 0) return;

        // 軸
        int w = getWidth();
        int h = getHeight();
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, h / 2, w, h / 2);

        // 最大値を求めてスケーリング
        double max = 1.0;
        for (double v : data) {
            if (Math.abs(v) > max) max = Math.abs(v);
        }

        // 信号グラフ描画
        g2.setColor(Color.BLUE);
        for (int i = 1; i < data.length; i++) {
            int x1 = (i - 1) * w / data.length;
            int y1 = h / 2 - (int) (data[i - 1] / max * h / 2);
            int x2 = i * w / data.length;
            int y2 = h / 2 - (int) (data[i] / max * h / 2);
            g2.drawLine(x1, y1, x2, y2);
        }
    }
}
