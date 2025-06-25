// package Fourier.view;

// import javax.swing.*;
// import java.awt.*;

// public class SignalWindow extends JFrame {
//     public SignalWindow(String title, double[] signalData) {
//         setTitle(title);
//         setSize(400, 300);
//         setLocationRelativeTo(null);
//         setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//         setLayout(new GridLayout(2, 2));

//         // 4つのグリッドを作成（左上のみ信号あり）
//         add(new SignalPanel(signalData, 1));
//         add(new SignalPanel(null, 2));
//         add(new SignalPanel(null, 3));
//         add(new SignalPanel(null, 4));
//     }
// }
