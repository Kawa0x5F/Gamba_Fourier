package Fourier.controller;

import java.awt.Cursor;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import Fourier.Menu;

public class FourierController extends MouseInputAdapter {
	Menu menu = new Menu();
	public static boolean Keepdate = false; // データを保存するのを制御するための変数
	public static boolean Indata = false; // データを入力するのを制御するための変数
	public static boolean Respectrum = false; // スペクトルの削除を制御をするための変数

	public void mouseReleased(MouseEvent sensing) {
	}

	public void mouseClicked(MouseEvent sensing) {
		switch (sensing.getButton()) {
			case MouseEvent.BUTTON1:
				sensing.getComponent().getWidth();
				Point coordinates = sensing.getPoint();
				System.out.println("x : " + coordinates.x + ", " +
						coordinates.y);
				break;

			case MouseEvent.BUTTON2:
				break;
			case MouseEvent.BUTTON3:
				menu.displayMenuScreen();
				break;
		}
	}

	public void mouseEntered(MouseEvent sensing) {
	}

	public void mouseDragged(MouseEvent sensing) {
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		Component component = (Component) sensing.getSource();
		component.setCursor(cursor);
	}

	public void mouseMoved(MouseEvent sensing) {
	}
}
