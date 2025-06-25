package Fourier.model;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public abstract class FourierModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

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
