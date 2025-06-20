package Fourier.controller;

import Fourier.model.FourierModel2D;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.event.InputEvent;

public class FourierController2D extends FourierController {

	private FourierModel2D fourierModel2D = new FourierModel2D();

	Point dragchange = null;

	private boolean isAltDown(MouseEvent sensing) {
		return (sensing.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
	}

	public void mouseClicked(MouseEvent sensing) {
		dragchange = sensing.getPoint();
		fourierModel2D.computeFromMousePoint(dragchange, isAltDown(sensing));
	}

	public void mousePressed(MouseEvent sensing) {
		dragchange = sensing.getPoint();
		fourierModel2D.computeFromMousePoint(dragchange, isAltDown(sensing));
	}

	public void mouseDragged(MouseEvent sensing) {
		dragchange = sensing.getPoint();
		fourierModel2D.computeFromMousePoint(dragchange, isAltDown(sensing));

	}

	public void mouseReleased(MouseEvent sensing) {
		dragchange = sensing.getPoint();
		fourierModel2D.computeFromMousePoint(dragchange, isAltDown(sensing));
	}

}
