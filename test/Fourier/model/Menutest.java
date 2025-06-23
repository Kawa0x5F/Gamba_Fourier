package Fourier.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import Fourier.controller.FourierController;
import Fourier.Menu;



class MenuTest {
    /**
     * MenuクラスのcallFileIOメソッドのテスト。
     * FileIOの代わりに手作りのモッククラスを使って、各メソッドが正しく呼び出されるかを確認する。
     */

    // FileIOの代わりに使う手作りのモッククラス
    //各メソッドが呼び出されたらtrueをセットする
    static class MockFileIO {
        boolean read1d = false, read2d = false, write1d = false, write2d = false;
        void readSignalFromCSV(String path) { read1d = true; }
        void readSignalFromImage(String path) { read2d = true; }
        void writeSignalToCSV(String path) { write1d = true; }
        void writeSignalToImage(String path) { write2d = true; }
    }

    // テスト用Menuサブクラス
    static class TestMenu extends Menu {
        MockFileIO fileIO = new MockFileIO();
        @Override public void callFileIO() {
            // 1次元データを読み込む
            if (FourierController.In1dData) fileIO.readSignalFromCSV("test.csv");
            // 2次元データを読み込む
            if (FourierController.In2dData) fileIO.readSignalFromImage("test.png");
            // 1次元データを保存する
            if (FourierController.Keepdata && FourierController.Dimensional == 0) fileIO.writeSignalToCSV("test.csv");
            // 2次元データを保存する
            if (FourierController.Keepdata && FourierController.Dimensional == 1) fileIO.writeSignalToImage("test.png");
        }
    }

    @Test
    @DisplayName("1次元データを読み込むメソッドのみが呼び出されるか")
    void test1dRead() {
        //1次元データ読み込みのみ有効
        FourierController.In1dData = true;
        FourierController.In2dData = false;
        FourierController.Keepdata = false;

        TestMenu menu = new TestMenu();
        menu.callFileIO();

        assertTrue(menu.fileIO.read1d);
        assertFalse(menu.fileIO.read2d);
        assertFalse(menu.fileIO.write1d);
        assertFalse(menu.fileIO.write2d);
    }

    @Test
    @DisplayName("2次元データを読み込むメソッドのみが呼び出されるか")
    void test2dRead() {
        //2次元データ読み込みのみ有効
        FourierController.In1dData = false;
        FourierController.In2dData = true;
        FourierController.Keepdata = false;

        TestMenu menu = new TestMenu();
        menu.callFileIO();

        assertFalse(menu.fileIO.read1d);
        assertTrue(menu.fileIO.read2d);
        assertFalse(menu.fileIO.write1d);
        assertFalse(menu.fileIO.write2d);
    }

    @Test
    @DisplayName("1次元データを保存するメソッドのみが呼び出されるか")
    void test1dSave() {
        //1次元データ保存のみ有効
        FourierController.In1dData = false;
        FourierController.In2dData = false;
        FourierController.Keepdata = true;
        FourierController.Dimensional = 0;

        TestMenu menu = new TestMenu();
        menu.callFileIO();

        assertFalse(menu.fileIO.read1d);
        assertFalse(menu.fileIO.read2d);
        assertTrue(menu.fileIO.write1d);
        assertFalse(menu.fileIO.write2d);
    }

    @Test
    @DisplayName("2次元データを保存するメソッドのみが呼び出されるか")
    void test2dSave() {
        //2次元データ保存のみ有効
        FourierController.In1dData = false;
        FourierController.In2dData = false;
        FourierController.Keepdata = true;
        FourierController.Dimensional = 1;

        TestMenu menu = new TestMenu();
        menu.callFileIO();

        assertFalse(menu.fileIO.read1d);
        assertFalse(menu.fileIO.read2d);
        assertFalse(menu.fileIO.write1d);
        assertTrue(menu.fileIO.write2d);
    }
}

