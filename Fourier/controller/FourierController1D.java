package Fourier.controller;

import Fourier.model.FourierModel1D;
import Fourier.model.FourierModel2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

public class FourierController1D extends FourierController {

	private FourierModel1D fourierModel1D = new FourierModel1D();

	Point dragchange = null;

	private boolean isAltDown(MouseEvent sensing) {
		return (sensing.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
	}

	public void mouseClicked(MouseEvent sensing) {
		dragchange = sensing.getPoint();
		fourierModel1D.computeFromMousePoint(dragchange, isAltDown(sensing));
	}

	public void mousePressed(MouseEvent sensing) {
		dragchange = sensing.getPoint();
		fourierModel1D.computeFromMousePoint(dragchange, isAltDown(sensing));
	}

	public void mouseDragged(MouseEvent sensing) {
		dragchange = sensing.getPoint();
		fourierModel1D.computeFromMousePoint(dragchange, isAltDown(sensing));

	}

	public void mouseReleased(MouseEvent sensing) {
		dragchange = sensing.getPoint();
		fourierModel1D.computeFromMousePoint(dragchange, isAltDown(sensing));
	}

}
