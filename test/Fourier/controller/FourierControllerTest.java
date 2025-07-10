package Fourier.controller;

import Fourier.model.FourierModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * FourierControllerクラスの単体テストクラス
 * マウスイベントの処理とモデルとの連携をテストする
 * 
 * @author Generated Test
 * @see FourierController
 */
class FourierControllerTest {

    private TestFourierModel testModel;
    private TestFourierController controller;
    private JPanel testComponent;

    /**
     * テスト用のFourierModelの実装
     */
    private static class TestFourierModel extends FourierModel {
        public List<Point> computeFromMousePointCalls = new ArrayList<>();
        public List<Boolean> altDownFlags = new ArrayList<>();
        
        @Override
        public void computeFromMousePoint(Point point, Boolean isAltDown) {
            computeFromMousePointCalls.add(new Point(point));
            altDownFlags.add(isAltDown);
        }
        
        @SuppressWarnings("unused")
        public void reset() {
            computeFromMousePointCalls.clear();
            altDownFlags.clear();
        }
        
        public int getComputeCallCount() {
            return computeFromMousePointCalls.size();
        }
    }

    /**
     * テスト用のFourierControllerの具象実装
     */
    private static class TestFourierController extends FourierController {
        public TestFourierController(FourierModel model) {
            super(model);
        }
    }

    @BeforeEach
    void setUp() {
        testModel = new TestFourierModel();
        controller = new TestFourierController(testModel);
        testComponent = new JPanel();
    }

    @Nested
    @DisplayName("マウスクリックイベントのテスト")
    class MouseClickedTest {

        @Test
        @DisplayName("左クリックでモデルの計算がトリガーされる")
        void testLeftClickTriggersComputation() {
            Point clickPoint = new Point(100, 50);
            MouseEvent leftClickEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_CLICKED, 
                System.currentTimeMillis(), 0, 
                clickPoint.x, clickPoint.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mouseClicked(leftClickEvent);

            assertEquals(1, testModel.getComputeCallCount());
            assertEquals(clickPoint, testModel.computeFromMousePointCalls.get(0));
            assertEquals(Boolean.FALSE, testModel.altDownFlags.get(0));
        }

        @Test
        @DisplayName("右クリックではモデルの計算がトリガーされない")
        void testRightClickDoesNotTriggerComputation() {
            Point clickPoint = new Point(100, 50);
            MouseEvent rightClickEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_CLICKED, 
                System.currentTimeMillis(), MouseEvent.BUTTON3_DOWN_MASK, 
                clickPoint.x, clickPoint.y, 1, false, MouseEvent.BUTTON3
            );

            controller.mouseClicked(rightClickEvent);

            assertEquals(0, testModel.getComputeCallCount());
        }

        @Test
        @DisplayName("Alt+左クリックでAltフラグがtrueで渡される")
        void testAltClickPassesAltFlag() {
            Point clickPoint = new Point(100, 50);
            MouseEvent altClickEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_CLICKED, 
                System.currentTimeMillis(), MouseEvent.ALT_DOWN_MASK, 
                clickPoint.x, clickPoint.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mouseClicked(altClickEvent);

            assertEquals(1, testModel.getComputeCallCount());
            assertEquals(clickPoint, testModel.computeFromMousePointCalls.get(0));
            assertEquals(Boolean.TRUE, testModel.altDownFlags.get(0));
        }

        @Test
        @DisplayName("複数の左クリックでそれぞれ計算がトリガーされる")
        void testMultipleLeftClicksTriggerMultipleComputations() {
            Point point1 = new Point(10, 20);
            Point point2 = new Point(30, 40);
            
            MouseEvent click1 = new MouseEvent(
                testComponent, MouseEvent.MOUSE_CLICKED, 
                System.currentTimeMillis(), 0, 
                point1.x, point1.y, 1, false, MouseEvent.BUTTON1
            );
            
            MouseEvent click2 = new MouseEvent(
                testComponent, MouseEvent.MOUSE_CLICKED, 
                System.currentTimeMillis(), 0, 
                point2.x, point2.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mouseClicked(click1);
            controller.mouseClicked(click2);

            assertEquals(2, testModel.getComputeCallCount());
            assertEquals(point1, testModel.computeFromMousePointCalls.get(0));
            assertEquals(point2, testModel.computeFromMousePointCalls.get(1));
        }
    }

    @Nested
    @DisplayName("マウス押下イベントのテスト")
    class MousePressedTest {

        @Test
        @DisplayName("左ボタン押下でモデルの計算がトリガーされる")
        void testLeftPressTriggersComputation() {
            Point pressPoint = new Point(75, 125);
            MouseEvent leftPressEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_PRESSED, 
                System.currentTimeMillis(), 0, 
                pressPoint.x, pressPoint.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mousePressed(leftPressEvent);

            assertEquals(1, testModel.getComputeCallCount());
            assertEquals(pressPoint, testModel.computeFromMousePointCalls.get(0));
            assertEquals(Boolean.FALSE, testModel.altDownFlags.get(0));
        }

        @Test
        @DisplayName("右ボタン押下ではモデルの計算がトリガーされない")
        void testRightPressDoesNotTriggerComputation() {
            Point pressPoint = new Point(75, 125);
            MouseEvent rightPressEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_PRESSED, 
                System.currentTimeMillis(), MouseEvent.BUTTON3_DOWN_MASK, 
                pressPoint.x, pressPoint.y, 1, false, MouseEvent.BUTTON3
            );

            controller.mousePressed(rightPressEvent);

            assertEquals(0, testModel.getComputeCallCount());
        }

        @Test
        @DisplayName("Alt+左ボタン押下でAltフラグがtrueで渡される")
        void testAltPressPassesAltFlag() {
            Point pressPoint = new Point(75, 125);
            MouseEvent altPressEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_PRESSED, 
                System.currentTimeMillis(), MouseEvent.ALT_DOWN_MASK, 
                pressPoint.x, pressPoint.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mousePressed(altPressEvent);

            assertEquals(1, testModel.getComputeCallCount());
            assertEquals(pressPoint, testModel.computeFromMousePointCalls.get(0));
            assertEquals(Boolean.TRUE, testModel.altDownFlags.get(0));
        }
    }

    @Nested
    @DisplayName("マウスドラッグイベントのテスト")
    class MouseDraggedTest {

        @Test
        @DisplayName("左ドラッグでモデルの計算がトリガーされる")
        void testLeftDragTriggersComputation() {
            Point dragPoint = new Point(200, 300);
            MouseEvent leftDragEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_DRAGGED, 
                System.currentTimeMillis(), 0, 
                dragPoint.x, dragPoint.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mouseDragged(leftDragEvent);

            assertEquals(1, testModel.getComputeCallCount());
            assertEquals(dragPoint, testModel.computeFromMousePointCalls.get(0));
            assertEquals(Boolean.FALSE, testModel.altDownFlags.get(0));
        }

        @Test
        @DisplayName("右ドラッグではモデルの計算がトリガーされない")
        void testRightDragDoesNotTriggerComputation() {
            Point dragPoint = new Point(200, 300);
            MouseEvent rightDragEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_DRAGGED, 
                System.currentTimeMillis(), MouseEvent.BUTTON3_DOWN_MASK, 
                dragPoint.x, dragPoint.y, 1, false, MouseEvent.BUTTON3
            );

            controller.mouseDragged(rightDragEvent);

            assertEquals(0, testModel.getComputeCallCount());
        }

        @Test
        @DisplayName("Alt+左ドラッグでAltフラグがtrueで渡される")
        void testAltDragPassesAltFlag() {
            Point dragPoint = new Point(200, 300);
            MouseEvent altDragEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_DRAGGED, 
                System.currentTimeMillis(), MouseEvent.ALT_DOWN_MASK, 
                dragPoint.x, dragPoint.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mouseDragged(altDragEvent);

            assertEquals(1, testModel.getComputeCallCount());
            assertEquals(dragPoint, testModel.computeFromMousePointCalls.get(0));
            assertEquals(Boolean.TRUE, testModel.altDownFlags.get(0));
        }

        @Test
        @DisplayName("連続ドラッグで複数回計算がトリガーされる")
        void testContinuousDragTriggersMutipleComputations() {
            Point[] dragPoints = {
                new Point(50, 50),
                new Point(60, 60),
                new Point(70, 70)
            };

            for (Point point : dragPoints) {
                MouseEvent dragEvent = new MouseEvent(
                    testComponent, MouseEvent.MOUSE_DRAGGED, 
                    System.currentTimeMillis(), 0, 
                    point.x, point.y, 1, false, MouseEvent.BUTTON1
                );
                controller.mouseDragged(dragEvent);
            }

            assertEquals(3, testModel.getComputeCallCount());
            for (int i = 0; i < dragPoints.length; i++) {
                assertEquals(dragPoints[i], testModel.computeFromMousePointCalls.get(i));
                assertEquals(Boolean.FALSE, testModel.altDownFlags.get(i));
            }
        }
    }

    @Nested
    @DisplayName("コンストラクタのテスト")
    class ConstructorTest {

        @Test
        @DisplayName("モデルがnullでないことを確認")
        void testConstructorWithValidModel() {
            assertNotNull(controller);
            // モデルのフィールドは protected なので直接アクセスできないが、
            // メソッドの動作を通じて間接的にテストできる
        }

        @Test
        @DisplayName("コンストラクタでnullモデルを渡すとNullPointerExceptionが発生する可能性がある")
        void testConstructorWithNullModel() {
            // nullモデルでコンストラクタを呼び出すテスト
            // 実際のnullチェックは実装に依存するため、例外が発生しなくても問題ない
            assertDoesNotThrow(() -> {
                new TestFourierController(null);
            });
        }
    }

    @Nested
    @DisplayName("統合テスト")
    class IntegrationTest {

        @Test
        @DisplayName("異なるマウスイベントが混在しても正しく処理される")
        void testMixedMouseEvents() {
            Point point = new Point(100, 100);
            
            // 左クリック
            MouseEvent click = new MouseEvent(
                testComponent, MouseEvent.MOUSE_CLICKED, 
                System.currentTimeMillis(), 0, 
                point.x, point.y, 1, false, MouseEvent.BUTTON1
            );
            
            // 右クリック
            MouseEvent rightClick = new MouseEvent(
                testComponent, MouseEvent.MOUSE_CLICKED, 
                System.currentTimeMillis(), MouseEvent.BUTTON3_DOWN_MASK, 
                point.x, point.y, 1, false, MouseEvent.BUTTON3
            );
            
            // 左ドラッグ
            MouseEvent drag = new MouseEvent(
                testComponent, MouseEvent.MOUSE_DRAGGED, 
                System.currentTimeMillis(), 0, 
                point.x, point.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mouseClicked(click);
            controller.mouseClicked(rightClick);
            controller.mouseDragged(drag);

            // 左クリックと左ドラッグのみでcomputeFromMousePointが呼ばれるはず
            assertEquals(2, testModel.getComputeCallCount());
            assertEquals(point, testModel.computeFromMousePointCalls.get(0));
            assertEquals(point, testModel.computeFromMousePointCalls.get(1));
        }

        @Test
        @DisplayName("座標の精度が正しく保たれる")
        void testCoordinatePrecision() {
            Point precisePoint = new Point(123, 456);
            MouseEvent preciseEvent = new MouseEvent(
                testComponent, MouseEvent.MOUSE_CLICKED, 
                System.currentTimeMillis(), 0, 
                precisePoint.x, precisePoint.y, 1, false, MouseEvent.BUTTON1
            );

            controller.mouseClicked(preciseEvent);

            // 正確な座標でcomputeFromMousePointが呼ばれることを確認
            assertEquals(1, testModel.getComputeCallCount());
            assertEquals(precisePoint, testModel.computeFromMousePointCalls.get(0));
        }
    }
}
