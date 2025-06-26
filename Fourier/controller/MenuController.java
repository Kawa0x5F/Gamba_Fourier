package Fourier.controller;

import Fourier.model.FourierModel;
import Fourier.Menu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 * マウスの右クリックを検知し、メニューを表示する責務を持つコントローラ。
 * このリスナーは、メニューを表示させたい全てのパネルに登録されることを想定している。
 */
public class MenuController extends MouseAdapter {

    private final Menu menu;

    /**
     * コンストラクタ。
     * @param model メニューが操作するモデル
     */
    public MenuController(FourierModel model) {
        this.menu = new Menu(model);
    }

    /**
     * マウスがクリックされた時の処理。
     * 右クリックの場合にのみメニューを表示する。
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // 右クリックの場合にメニューを表示
        if (SwingUtilities.isRightMouseButton(e)) {
            menu.displayMenuScreen(e.getComponent(), e.getX(), e.getY());
        }
    }
}