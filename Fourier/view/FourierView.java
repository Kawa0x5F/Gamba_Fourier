package Fourier.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;

import Fourier.model.FourierModel;

public abstract class FourierView implements PropertyChangeListener {
    private final FourierModel model;

    public FourierView(FourierModel model) {
        this.model = model;

        // 自分自身をモデルのリスナーとして登録
        this.model.addPropertyChangeListener(this);
    }

    /**
     * モデルへの参照を小クラスに提供するためのメソッド
     */
    protected FourierModel getModel() {
        return this.model;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        updateView();
    }

    protected abstract void updateView();
}
