package Fourier.model;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * フーリエ変換モデルの抽象基底クラス。
 * 1次元と2次元のフーリエ変換モデルで共通する機能を提供します。
 */
public abstract class FourierModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected int brushSize = 1;

    /**
     * ブラシサイズを設定します。
     * @param brushSize 新しいブラシの半径
     */
    public void setBrushSize(int brushSize) {
        this.brushSize = Math.max(0, brushSize); // 0未満にならないように制御
    }

    /**
     * 現在のブラシサイズを取得します。
     * @return ブラシの半径
     */
    public int getBrushSize() {
        return this.brushSize;
    }

    /**
     * プロパティ変更リスナーを追加します。
     * @param listener 追加するリスナー
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * プロパティ変更リスナーを削除します。
     * @param listener 削除するリスナー
     */
    public void removePropertyChangeLisener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * マウス座標に基づいて計算を実行します。
     * @param point マウス座標
     * @param isAltDown Altキーが押されているかどうか
     */
    public abstract void computeFromMousePoint(Point point, Boolean isAltDown);

    /**
     * パネルサイズを考慮したマウス座標計算（オーバーロード）
     * デフォルト実装では既存のメソッドを呼び出します。
     * @param point マウス座標
     * @param isAltDown Altキーが押されているかどうか
     * @param panelWidth パネルの幅
     * @param panelHeight パネルの高さ
     */
    public void computeFromMousePoint(Point point, Boolean isAltDown, int panelWidth, int panelHeight) {
        computeFromMousePoint(point, isAltDown);
    }

    /**
     * プロパティ変更イベントを発生させます。
     * @param propertyName プロパティ名
     * @param oldValue 古い値
     * @param newValue 新しい値
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }
}
