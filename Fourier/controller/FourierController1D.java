package Fourier.controller;

import Fourier.model.FourierModel1D;
import Fourier.model.FourierModel2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class FourierController1D extends FourierController {

	private FourierModel2D fourierModel2D;

	private FourierModel1D fourierModel1D;

	Point coordinates_before = null;
	Point coordinates_after = null;
	boolean isDragging = false;

	public void mousePressed(MouseEvent sensing) {
		coordinates_before = sensing.getPoint();
		isDragging = false;
	}

	public void mouseDragged(MouseEvent sensing) {
		isDragging = true;

	}

	public void mouseReleased(MouseEvent sensing) {
		if (isDragging) {
			coordinates_after = sensing.getPoint();

		}
	}

}
