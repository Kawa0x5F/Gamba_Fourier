package Fourier.view;

import javax.swing.*;
import java.awt.*;

public class SignalPanel extends JPanel {
    private double[] data;
    private String title; // グリッド番号からタイトルに変更

    // コンストラクタ：初期データはnullでも可
    public SignalPanel(String title) {
        this.title = title;
        this.data = null;
        setBackground(Color.WHITE);
    }

    /**
     * 表示する信号データを設定し、再描画を要求するメソッド
     * @param data 表示するデータ配列
     */
    public void setData(double[] data) {
        this.data = data;
        repaint(); // データが設定されたら再描画をトリガー
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // タイトルを左上に描画
        g2.setColor(Color.BLACK);
        g2.drawString(title, 10, 15);

        // 信号データがなければ終了
        if (data == null || data.length == 0) return;

        int w = getWidth();
        int h = getHeight();

        // X軸（中央線）
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, h / 2, w, h / 2);

        // 信号の最大絶対値を求めてスケーリング
        double max = 0.0;
        for (double v : data) {
            if (Math.abs(v) > max) {
                max = Math.abs(v);
            }
        }
        if (max == 0) max = 1.0; // ゼロ除算を避ける

        // 信号グラフ描画
        g2.setColor(Color.BLUE);
        for (int i = 0; i < data.length - 1; i++) {
            int x1 = i * w / data.length;
            int y1 = h / 2 - (int) (data[i] / max * (h / 2.0));
            int x2 = (i + 1) * w / data.length;
            int y2 = h / 2 - (int) (data[i + 1] / max * (h / 2.0));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * (テスト用) 現在設定されているデータを取得する
     * @return 信号データ配列
     */
    public double[] getData() {
        return this.data;
    }
}