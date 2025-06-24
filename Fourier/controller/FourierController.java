package Fourier.controller;

import Fourier.view.FourierView;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import Fourier.Menu;

public class FourierController extends MouseInputAdapter {
	Menu menu = new Menu();
	public static boolean Keepdata = false; // データを保存するのを制御するための変数
	public static boolean In1dData = false; // データを入力するのを制御するための変数
	public static boolean In2dData = false; // データを入力するのを制御するための変数
	public static boolean Respectrum = false; // スペクトルの削除を制御をするための変数
	private boolean leftPressed = false;

	public void mouseReleased(MouseEvent sensing) {
		leftPressed = false;
	}

	public void mouseClicked(MouseEvent sensing) {
		if (sensing.getButton() == MouseEvent.BUTTON3) {
			menu.displayMenuScreen();
		}
	}

	public void mouseEntered(MouseEvent sensing) {
	}

	public void mousePressed(MouseEvent sensing) {
	}

	public void mouseDragged(MouseEvent sensing) {
		if (leftPressed) {
			Cursor cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
			Component component = (Component) sensing.getSource();
			component.setCursor(cursor);
		}
	}

	public void mouseMoved(MouseEvent sensing) {
	}
}
