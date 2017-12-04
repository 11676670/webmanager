package com.hc.webstart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.alibaba.fastjson.JSON;
import com.hc.dto.ResponseMsg;
import com.hc.dto.SiteAccount;
import com.hc.http.HttpRequester;
import com.hc.http.HttpResponser;

public class MainLoginUI extends JFrame {

    private static final long serialVersionUID = -7057133439521815110L;

   private static JFrame loginDalog;
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            loginDalog = new MainLoginUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public MainLoginUI() {
        //获取当前用户的账号系统
        
        
        setResizable(false);
        // 设置大小
        this.setSize(302, 287);
        // 设置标题
        this.setTitle("登录界面");
        JLabel unameLabel = new JLabel("账号：");
        JTextField unameField = new JTextField();
        unameField.setText("user");
        JLabel upsdLabel = new JLabel("密码：");
        JTextField upsdField = new JTextField();
        upsdField.setText("123456");
        JButton login = new JButton("登录");
        JButton cancel = new JButton("取消");
        // 设置位置与大小
        unameLabel.setBounds(50, 50, 50, 30);
        unameField.setBounds(100, 50, 150, 30);
        upsdLabel.setBounds(50, 100, 50, 30);
        upsdField.setBounds(100, 100, 150, 30);
        login.setBounds(60, 160, 60, 40);
        cancel.setBounds(160, 160, 60, 40);
        // 设置布局为空，使用坐标控制控件位置的时候，一定要设置布局为空
        getContentPane().setLayout(null);
        // 添加控件
        getContentPane().add(login);
        getContentPane().add(cancel);
        getContentPane().add(unameField);
        getContentPane().add(unameLabel);
        getContentPane().add(upsdLabel);
        getContentPane().add(upsdField);
        // 设置dislog的相对位置，参数为null，即显示在屏幕中间
        this.setLocationRelativeTo(null);
        // 设置当用户在此对话框上启动 "close" 时默认执行的操作
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置是否显示
        this.setVisible(true);
        
        cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
              /*  loginDalog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
               * System.exit(0);
                loginDalog.dispose();
                loginDalog.setVisible(true);*/
                System.exit(0);
            }
        });
        login.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                HttpRequester request = new HttpRequester();
                try {
                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("userName", unameField.getText());
                    paramMap.put("password", upsdField.getText());
                    HttpResponser response = request.sendPost(Global.buildUrl("userinfo"), paramMap);
                    //{"msg":"保存成功!","result":[{"webUrl":"http://hcptsp.sun-it.cn/f/hcptsp/user/login.html","userName":"admin","passWord":"123456","userType":0}],"code":"200","status":true}
                    ResponseMsg resultMsg = JSON.parseObject(response.getContent(), ResponseMsg.class);
                    if(resultMsg.getResult() != null) {
                        for (SiteAccount account : resultMsg.getResult()) {
                            Global.siteAccountMap.put(account.getWebUrl(), account);
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
              loginDalog.dispose();
              new MainApplication();
            }
        });
    }

}
