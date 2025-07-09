// FourierController.java の修正版

package Fourier.controller;

import Fourier.model.FourierModel;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.SwingUtilities; // SwingUtilitiesをインポート

/**
 * マウスの左クリックとドラッグ入力をモデルに伝え、スペクトル計算をトリガーする責務を持つ
 * コントローラの抽象親クラス。（右クリック処理を分離）
 */
public abstract class FourierController extends MouseInputAdapter {

    protected final FourierModel model;
    // Menuへの参照は不要になるため削除
    // protected final Menu menu;
    
    // カーソル管理用の変数
    private Cursor originalCursor;
    private static final Cursor CROSSHAIR_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    /**
     * コンストラクタ。
     * @param model このコントローラが操作するモデル
     */
    public FourierController(FourierModel model) {
        this.model = model;
        // Menuのインスタンス化を削除
    }

    /**
     * マウスがクリックされた時の処理。
     * 右クリックでない場合（主に左クリック）にモデルの計算をトリガーする。
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {
            model.computeFromMousePoint(e.getPoint(), e.isAltDown());
        }
    }

    /**
     * マウスボタンが押された時の処理。
     * 右クリックでない場合にモデルの計算をトリガーし、カーソルを十字形に変更する。
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {
            // 元のカーソルを保存してから十字形に変更
            Component source = (Component) e.getSource();
            originalCursor = source.getCursor();
            source.setCursor(CROSSHAIR_CURSOR);
            
            model.computeFromMousePoint(e.getPoint(), e.isAltDown());
        }
    }

    /**
     * マウスボタンが離された時の処理。
     * カーソルを元の形状に戻す。
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {
            // カーソルを元の形状に戻す
            Component source = (Component) e.getSource();
            if (originalCursor != null) {
                source.setCursor(originalCursor);
            } else {
                source.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    /**
     * マウスがドラッグされた時の処理。
     * 右ドラッグでない場合にモデルの計算を連続してトリガーする。
     * カーソルは既に十字形に設定されているので変更しない。
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {
            model.computeFromMousePoint(e.getPoint(), e.isAltDown());
        }
    }
}