package com.renda;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class Main extends JFrame implements NativeKeyListener {
    private JTextField intervalField;
    private JTextField toggleKeyField;
    private JButton startButton;
    private JButton stopButton;
    private Robot robot;
    private volatile boolean running = false;
    private volatile boolean isEnabled = false; // Nキーが有効かどうか
    private int clickInterval = 100; // 初期クリック間隔（ミリ秒）
    private char toggleKey = 'N'; // 初期トグルキー
    private Map<Character, Integer> keyMap; // 文字とキーコードの対応を保持

    public Main() {
        setTitle("Auto Clicker");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        // クリック間隔の設定
        JPanel intervalPanel = new JPanel(new FlowLayout());
        intervalPanel.add(new JLabel("クリック間隔（ミリ秒）:"));
        intervalField = new JTextField(String.valueOf(clickInterval), 10);
        intervalPanel.add(intervalField);
        add(intervalPanel);

        // トグルキーの設定
        JPanel toggleKeyPanel = new JPanel(new FlowLayout());
        toggleKeyPanel.add(new JLabel("トグルキー:"));
        toggleKeyField = new JTextField(String.valueOf(toggleKey), 10);
        toggleKeyPanel.add(toggleKeyField);
        add(toggleKeyPanel);

        // スタートボタン
        startButton = new JButton("開始");
        startButton.addActionListener(new StartAction());
        add(startButton);

        // ストップボタン
        stopButton = new JButton("停止");
        stopButton.setEnabled(false); // 初期状態では停止ボタンは無効
        stopButton.addActionListener(new StopAction());
        add(stopButton);

        setVisible(true);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // 文字とキーコードの対応を初期化
        initializeKeyMap();

        // JNativeHookの初期化
        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(this);
    }

    private void initializeKeyMap() {
        keyMap = new HashMap<>();
        keyMap.put('A', NativeKeyEvent.VC_A);
        keyMap.put('B', NativeKeyEvent.VC_B);
        keyMap.put('C', NativeKeyEvent.VC_C);
        keyMap.put('D', NativeKeyEvent.VC_D);
        keyMap.put('E', NativeKeyEvent.VC_E);
        keyMap.put('F', NativeKeyEvent.VC_F);
        keyMap.put('G', NativeKeyEvent.VC_G);
        keyMap.put('H', NativeKeyEvent.VC_H);
        keyMap.put('I', NativeKeyEvent.VC_I);
        keyMap.put('J', NativeKeyEvent.VC_J);
        keyMap.put('K', NativeKeyEvent.VC_K);
        keyMap.put('L', NativeKeyEvent.VC_L);
        keyMap.put('M', NativeKeyEvent.VC_M);
        keyMap.put('N', NativeKeyEvent.VC_N);
        keyMap.put('O', NativeKeyEvent.VC_O);
        keyMap.put('P', NativeKeyEvent.VC_P);
        keyMap.put('Q', NativeKeyEvent.VC_Q);
        keyMap.put('R', NativeKeyEvent.VC_R);
        keyMap.put('S', NativeKeyEvent.VC_S);
        keyMap.put('T', NativeKeyEvent.VC_T);
        keyMap.put('U', NativeKeyEvent.VC_U);
        keyMap.put('V', NativeKeyEvent.VC_V);
        keyMap.put('W', NativeKeyEvent.VC_W);
        keyMap.put('X', NativeKeyEvent.VC_X);
        keyMap.put('Y', NativeKeyEvent.VC_Y);
        keyMap.put('Z', NativeKeyEvent.VC_Z);
    }

    private class StartAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                clickInterval = Integer.parseInt(intervalField.getText());

                // トグルキーの変更
                String toggleKeyText = toggleKeyField.getText().toUpperCase();
                if (toggleKeyText.length() == 1 && keyMap.containsKey(toggleKeyText.charAt(0))) {  // 入力が1文字で、対応するキーコードが存在する場合のみ更新
                    toggleKey = toggleKeyText.charAt(0);
                } else {
                    JOptionPane.showMessageDialog(Main.this, "トグルキーには有効な1文字を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                isEnabled = true; // トグルキーの反応を有効にする
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(Main.this, "有効な数値を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class StopAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            running = false;
            isEnabled = false; // トグルキーの反応を無効にする
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    private void startAutoClick() {
        running = true;
        new Thread(() -> {
            while (running) {
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);  // 左クリック押下
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); // 左クリックリリース
                try {
                    Thread.sleep(clickInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopAutoClick() {
        running = false;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int keyCode = e.getKeyCode();  // キーコードを取得

        if (isEnabled && keyCode == keyMap.get(toggleKey)) {  // トグルキーが押されたかどうかを確認
            if (running) {
                stopAutoClick();
            } else {
                startAutoClick();
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Do nothing
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Do nothing
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
