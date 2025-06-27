package Fourier.model;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeLisener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public abstract void computeFromMousePoint(Point point, Boolean isAltDown);

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }
}
