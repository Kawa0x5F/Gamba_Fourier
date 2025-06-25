package Fourier.controller;

import Fourier.model.FourierModel1D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

public class FourierController1D extends MouseAdapter {

    private FourierModel1D fourierModel1D;

    public FourierController1D(FourierModel1D model) {
        this.fourierModel1D = model;
    }

    private boolean isAltDown(MouseEvent sensing) {
        return (sensing.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
    }

    @Override
    public void mouseClicked(MouseEvent sensing) {
        Point clickPoint = sensing.getPoint();
        boolean altStatus = isAltDown(sensing);
        System.out.println("Controller: Mouse Clicked at " + clickPoint + ", Alt key down: " + altStatus); // デバッグ出力
        fourierModel1D.computeFromMousePoint(clickPoint, altStatus);
    }

    @Override
    public void mousePressed(MouseEvent sensing) {
        Point pressPoint = sensing.getPoint();
        boolean altStatus = isAltDown(sensing);
        System.out.println("Controller: Mouse Pressed at " + pressPoint + ", Alt key down: " + altStatus); // デバッグ出力
        fourierModel1D.computeFromMousePoint(pressPoint, altStatus);
    }

    @Override
    public void mouseDragged(MouseEvent sensing) {
        Point dragPoint = sensing.getPoint();
        boolean altStatus = isAltDown(sensing);
        System.out.println("Controller: Mouse Dragged at " + dragPoint + ", Alt key down: " + altStatus); // デバッグ出力
        fourierModel1D.computeFromMousePoint(dragPoint, altStatus);
    }

    @Override
    public void mouseReleased(MouseEvent sensing) {
        Point releasePoint = sensing.getPoint();
        boolean altStatus = isAltDown(sensing);
        System.out.println("Controller: Mouse Released at " + releasePoint + ", Alt key down: " + altStatus); // デバッグ出力
        fourierModel1D.computeFromMousePoint(releasePoint, altStatus);
    }
}