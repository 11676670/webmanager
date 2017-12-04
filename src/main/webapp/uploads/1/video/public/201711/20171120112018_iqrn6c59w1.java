package com.hc.webstart;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class TestFrame extends JFrame {

    private JPanel topPanel;
    
    private JPanel contentPane;

    private JPanel middlePanel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TestFrame frame = new TestFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public TestFrame() {
     /*   setTitle("自动登录控制台");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 474, 387);*/
        
        createTopPanel();
        
        /*contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));*/
        
        createMiddlePanel();
        
        
        JPanel panelContainer = new JPanel(); 
        //panelContainer 的布局为 GridBagLayout 
        panelContainer.setLayout(new GridBagLayout()); 
        
        GridBagConstraints c1 = new GridBagConstraints(); 
        c1.gridx = 0; 
        c1.gridy = 0; 
        c1.weightx = 1.0; 
        c1.weighty = 1.0; 
        c1.fill = GridBagConstraints.BOTH ; 
                // 加入 topPanel 
        panelContainer.add(topPanel, c1); 
        
        GridBagConstraints c2 = new GridBagConstraints(); 
        c2.gridx = 0; 
        c2.gridy = 1; 
        c2.weightx = 1.0; 
        c2.weighty = 0; 
        c2.fill = GridBagConstraints.HORIZONTAL ; 
                // 加入 middlePanel 
        panelContainer.add(middlePanel, c2); 
        
        GridBagConstraints c3 = new GridBagConstraints(); 
        c3.gridx = 0; 
        c3.gridy = 2; 
        c3.weightx = 1.0; 
        c3.weighty = 0; 
        c3.fill = GridBagConstraints.HORIZONTAL ; 
                // 加入 bottomPanel 
/*        panelContainer.add(bottomPanel, c3); */
               
                // 创建窗体
        JFrame frame = new JFrame("自动登录控制台"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE ); 
        panelContainer.setOpaque(true); 
        frame.setSize(new Dimension(480, 320)); 
        frame.setContentPane(panelContainer); 
        frame.setVisible(true); 
    }

    public void createTopPanel() {
        topPanel = new JPanel();
        String[] columnName = {"姓名", "性别", "单位", "参加项目", "备注" };
        String[][] rowData = {{"张三", "男", "计算机系", "100 米 ,200 米", "" }, {"李四", "男", "化学系", "100 米, 铅球", "" }};
       // 创建表格
        JTable table = new JTable(new DefaultTableModel(rowData, columnName));
       // 创建包含表格的滚动窗格
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
       // 定义 topPanel 的布局为 BoxLayout, BoxLayout 为垂直排列
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
       // 先加入一个不可见的 Strut, 从而使 topPanel 对顶部留出一定的空间
        topPanel.add(Box.createVerticalStrut(10));
       // 加入包含表格的滚动窗格 
        topPanel.add(scrollPane);
       // 再加入一个不可见的 Strut, 从而使 topPanel 和 middlePanel 之间留出一定的空间
         topPanel.add(Box.createVerticalStrut(10));
    }
    
    public void createMiddlePanel() { 
        // 创建 middlePanel 
        middlePanel = new JPanel(); 
        // 采用水平布局
        middlePanel .setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS )); 
        // 创建标签运动会项目        
                JLabel sourceLabel = new JLabel("运动会项目："); 
        sourceLabel.setAlignmentY(Component.TOP_ALIGNMENT ); 
        sourceLabel.setBorder(BorderFactory.createEmptyBorder (4, 5, 0, 5)); 
        // 创建列表运动会项目
        DefaultListModel listModel = new DefaultListModel(); 
        listModel.addElement("100 米"); 
        listModel.addElement("200 米"); 
        listModel.addElement("400 米"); 
        listModel.addElement("跳远"); 
        listModel.addElement("跳高"); 
        listModel.addElement("铅球"); 
        JList sourceList = new JList(listModel); 
        sourceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ); 
        sourceList.setVisibleRowCount(5); 
        JScrollPane sourceListScroller = new JScrollPane(sourceList); 
        sourceListScroller.setPreferredSize(new Dimension(120, 80)); 
        sourceListScroller 
        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ); 
        sourceListScroller.setAlignmentY(Component.TOP_ALIGNMENT ); 
        // 创建最左边的 Panel 
        JPanel sourceListPanel = new JPanel(); 
        // 最左边的 Panel 采用水平布局
        sourceListPanel.setLayout(new BoxLayout(sourceListPanel, 
        BoxLayout.X_AXIS )); 
        // 加入标签到最左边的 Panel 
        sourceListPanel.add(sourceLabel); 
        // 加入列表运动会项目到最左边的 Panel 
        sourceListPanel.add(sourceListScroller); 
        sourceListPanel.setAlignmentY(Component.TOP_ALIGNMENT ); 
        sourceListPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 0, 30)); 
        // 将最左边的 Panel 加入到 middlePanel 
        middlePanel .add(sourceListPanel); 
        // 定义中间的两个按钮        
        JButton toTargetButton = new JButton(">>"); 
        JButton toSourceButton = new JButton("<<"); 
        // 定义中间的 Panel 
        JPanel buttonPanel = new JPanel(); 
        // 中间的 Panel 采用水平布局
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS )); 
        // 将按钮 >> 加入到中间的 Panel 
        buttonPanel.add(toTargetButton); 

       //两个按钮之间加入一个不可见的 rigidArea            
        buttonPanel.add(Box.createRigidArea (new Dimension(15, 15))); 
        // 将按钮 << 加入到中间的 Panel 
        buttonPanel.add(toSourceButton); 
        buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT ); 
        buttonPanel.setBorder(BorderFactory.createEmptyBorder (15, 5, 15, 5)); 
        // 将中间的 Panel 加入到 middlePanel 
        middlePanel .add(buttonPanel); 
        // 创建标签查询项目
        JLabel targetLabel = new JLabel("查询项目："); 
        targetLabel.setAlignmentY(Component.TOP_ALIGNMENT ); 
        targetLabel.setBorder(BorderFactory.createEmptyBorder (4, 5, 0, 5)); 

       // 创建列表查询项目            
        DefaultListModel targetListModel = new DefaultListModel(); 
        targetListModel.addElement("100 米"); 
        JList targetList = new JList(targetListModel); 
        targetList 
        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ); 
        targetList.setVisibleRowCount(5); 
        JScrollPane targetListScroller = new JScrollPane(targetList); 
        targetListScroller.setPreferredSize(new Dimension(120, 80)); 
        targetListScroller 
        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ); 
        targetListScroller.setAlignmentY(Component.TOP_ALIGNMENT ); 
        // 创建最右边的 Panel 
        JPanel targetListPanel = new JPanel(); 
        // 设置最右边的 Panel 为水平布局
        targetListPanel.setLayout(new BoxLayout(targetListPanel, 
        BoxLayout.X_AXIS )); 
        // 将标签查询项目加到最右边的 Panel 
        targetListPanel.add(targetLabel); 
        // 将列表查询项目加到最右边的 Panel 
        targetListPanel.add(targetListScroller); 
        targetListPanel.setAlignmentY(Component.TOP_ALIGNMENT ); 
        targetListPanel.setBorder(BorderFactory.createEmptyBorder (0, 30, 0, 0)); 
        // 最后将最右边的 Panel 加入到 middlePanel 
        middlePanel .add(targetListPanel); 
    }
}