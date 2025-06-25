package Fourier.controller;

import Fourier.model.FourierModel2D; // Model2Dのインポート
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter; // MouseAdapterをインポート

// FourierControllerを継承せず、MouseAdapterを継承
public class FourierController2D extends MouseAdapter {

    private FourierModel2D fourierModel2D; // Modelのインスタンスは外部から注入されるべき

    // Point dragchange = null; // computeFromMousePointに直接Pointを渡すため不要

    // コンストラクタでModelのインスタンスを受け取るように変更
    public FourierController2D(FourierModel2D model) {
        this.fourierModel2D = model;
    }

    // Altキーが押されているか判定するヘルパーメソッド
    private boolean isAltDown(MouseEvent sensing) {
        return (sensing.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
    }

    @Override
    public void mouseClicked(MouseEvent sensing) {
        Point clickPoint = sensing.getPoint();
        boolean altStatus = isAltDown(sensing);
        System.out.println("Controller2D: Mouse Clicked at " + clickPoint + ", Alt key down: " + altStatus); // デバッグ出力
        fourierModel2D.computeFromMousePoint(clickPoint, altStatus);
    }

    @Override
    public void mousePressed(MouseEvent sensing) {
        Point pressPoint = sensing.getPoint();
        boolean altStatus = isAltDown(sensing);
        System.out.println("Controller2D: Mouse Pressed at " + pressPoint + ", Alt key down: " + altStatus); // デバッグ出力
        fourierModel2D.computeFromMousePoint(pressPoint, altStatus);
    }

    @Override
    public void mouseDragged(MouseEvent sensing) {
        Point dragPoint = sensing.getPoint();
        boolean altStatus = isAltDown(sensing);
        System.out.println("Controller2D: Mouse Dragged at " + dragPoint + ", Alt key down: " + altStatus); // デバッグ出力
        fourierModel2D.computeFromMousePoint(dragPoint, altStatus);
    }

    @Override
    public void mouseReleased(MouseEvent sensing) {
        Point releasePoint = sensing.getPoint();
        boolean altStatus = isAltDown(sensing);
        System.out.println("Controller2D: Mouse Released at " + releasePoint + ", Alt key down: " + altStatus); // デバッグ出力
        fourierModel2D.computeFromMousePoint(releasePoint, altStatus);
    }
}