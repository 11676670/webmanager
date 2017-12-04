package com.hc.webstart;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.openqa.selenium.WebDriver;

import com.hc.auto.SeleniumUtils;

public class MainUI extends JFrame {
    private static final long serialVersionUID = 2874903072858617727L;

    private JFrame jFrame;

    private JLabel label;

    private JButton whpButton;

    public MainUI() {
        jFrame = new JFrame("auto login panel");
        jFrame.setSize(470, 310);
        jFrame.getContentPane().setLayout(null);
        label = new JLabel(System.getProperty("user.dir"));
        label.setLocation(32, 21);
        label.setSize(358, 31);
        whpButton = new JButton("危化品登录");
        whpButton.setLocation(32, 78);
        whpButton.setSize(105, 43);
        jFrame.setBackground(Color.WHITE);
        jFrame.setLocation(400, 300);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        jFrame.getContentPane().add(label);
        jFrame.getContentPane().add(whpButton);
        whpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                whpButton.setBackground(Color.red);
                try {
                    WebDriver driver = SeleniumUtils.getIEDriver();
                    driver.get("http://www.baidu.com");
              //      label.setText(HcptspAutoLogin.login(driver));
                }catch(Exception ex) {
                    label.setText(ex.getMessage());
                }
            }
        });
    }

    // webstart 启动时执行的主方法
    public static void main(String args[]) {
        new MainUI();
    }
}