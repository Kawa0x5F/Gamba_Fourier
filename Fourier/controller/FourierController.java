package Fourier.controller;

import Fourier.model.FourierModel;
import Fourier.Menu;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

/**
 * マウス入力をモデルに伝え、右クリックでメニューを表示する責務を持つ
 * コントローラの抽象親クラス。
 */
public abstract class FourierController extends MouseInputAdapter {

    // モデルとメニューへの参照は protected とし、サブクラスからアクセス可能にする
    protected final FourierModel model;
    protected final Menu menu;

    /**
     * コンストラクタ。
     * @param model このコントローラが操作するモデル
     */
    public FourierController(FourierModel model) {
        this.model = model;
        this.menu = new Menu(model); // MenuにModelを渡して生成
    }

    /**
     * マウスがクリックされた時の処理。
     * 右クリックの場合はメニューを表示し、それ以外の場合はモデルの計算をトリガーする。
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            menu.displayMenuScreen(e.getComponent(), e.getX(), e.getY());
        } else {
            // 左クリックでもスペクトルを更新
            model.computeFromMousePoint(e.getPoint(), e.isAltDown());
        }
    }

    /**
     * マウスボタンが押された時の処理。
     * モデルの計算をトリガーする。
     */
    @Override
    public void mousePressed(MouseEvent e) {
        model.computeFromMousePoint(e.getPoint(), e.isAltDown());
    }

    /**
     * マウスがドラッグされた時の処理。
     * モデルの計算を連続してトリガーする。
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        model.computeFromMousePoint(e.getPoint(), e.isAltDown());
    }
}