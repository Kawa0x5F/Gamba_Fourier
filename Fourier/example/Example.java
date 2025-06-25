package Fourier.example;

import javax.swing.SwingUtilities;
import Fourier.model.FourierModel1D;
import Fourier.view.FourierView1D;
import Fourier.FourierData;
import Fourier.controller.FourierController1D;

public class Example {

    @SuppressWarnings("unused")
    private static FourierView1D view;
    @SuppressWarnings("unused")
    private static FourierController1D controller; 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. オリジナル信号データを準備
            double[] signalData = FourierData.dataSampleWave(); 
            System.out.println("Main: Initial signalData length: " + signalData.length); // デバッグ出力

            // 2. モデルのインスタンス化
            // FourierModel1DのコンストラクタがinitialOriginDataを受け取るように変更されたため、ここで渡す
            FourierModel1D model = new FourierModel1D(signalData); 
            System.out.println("Main: FourierModel1D initialized."); // デバッグ出力

            // 3. ビューのインスタンス化
            // ViewはModelのデータ構造の変更に対応済み
            view = new FourierView1D(model);
            System.out.println("Main: FourierView1D initialized."); // デバッグ出力

            // 4. コントローラーのインスタンス化とビューへのアタッチ
            // ControllerはModelを受け取るように変更されたため、ここで渡す
            controller = new FourierController1D(model); 
            // ViewのパネルにマウスリスナーとしてControllerをアタッチする
            // ここでは一例として、すべてのパネルにControllerをアタッチすると仮定します
            // 実際には、マウス操作を受け取る特定のパネル（例えば、再計算入力データ表示パネル）のみにアタッチする方が適切かもしれません
            view.addMouseListenerToAllPanels(controller); // FourierViewにこのメソッドを追加する必要があります
            view.addMouseMotionListenerToAllPanels(controller); // FourierViewにこのメソッドを追加する必要があります
            System.out.println("Main: FourierController1D initialized and attached to view panels."); // デバッグ出力

            // モデルのコンストラクタで初期計算がトリガーされるため、
            // ここで model.setCalculatedData(signalData); を明示的に呼ぶ必要はなくなります。
            // もし初回表示で「オリジナル信号」パネルに初期データから計算したスペクトルを表示したい場合は、
            // model.setCalculatedData(model.getInitialOriginData()); のような形で一度呼ぶことも考えられますが、
            // 現在のModelの設計ではspectrumRecalculationInputDataを基に計算するので、
            // 既にrecalculateSpectrum()が呼ばれて初期状態の0からのスペクトルが計算されています。
            // もし、起動時にinitialOriginDataのスペクトルも見せたいなら、Modelに別途そのためのフィールドと計算メソッドが必要になります。
            
            // 例: もしinitialOriginDataのスペクトルも表示したい場合
            // double[] initialSpectrum = model.calculateSpectrumFromInitialData(); // Modelにこのメソッドが必要
            // ((SignalPanel) view.getPanel("Initial Spectrum")).setData(initialSpectrum); // Viewに新しいパネルが必要
        });
    }
}
