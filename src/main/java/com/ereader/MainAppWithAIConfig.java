package com.ereader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

public class MainAppWithAIConfig extends JFrame {
    private static final String CONFIG_FILE = "config.properties";

    public MainAppWithAIConfig() {
        setTitle("主程序");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 设置菜单
        JMenuBar menuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("设置");
        JMenuItem configAIItem = new JMenuItem("配置 AI");

        configAIItem.addActionListener(e -> openConfigDialog());

        settingsMenu.add(configAIItem);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);

        JLabel infoLabel = new JLabel("欢迎使用主程序", SwingConstants.CENTER);
        add(infoLabel, BorderLayout.CENTER);
    }

    private void openConfigDialog() {
        JDialog dialog = new JDialog(this, "AI 配置", true);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);

        // 创建组件
        JLabel endpointLabel = new JLabel("接口地址：");
        JTextField endpointField = new JTextField(20);

        JLabel apiKeyLabel = new JLabel("API Key：");
        JPasswordField apiKeyField = new JPasswordField(20);

        JButton saveButton = new JButton("保存配置");

        // 加载配置
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
            endpointField.setText(props.getProperty("api.endpoint", ""));
            apiKeyField.setText(props.getProperty("api.key", ""));
        } catch (IOException ignored) {}

        // 创建面板与布局
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(endpointLabel)
                                .addComponent(endpointField))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(apiKeyLabel)
                                .addComponent(apiKeyField))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(saveButton))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(endpointLabel)
                                .addComponent(endpointField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(apiKeyLabel)
                                .addComponent(apiKeyField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(saveButton))
        );

        saveButton.addActionListener(e -> {
            String endpoint = endpointField.getText();
            String apiKey = new String(apiKeyField.getPassword());

            props.setProperty("api.endpoint", endpoint);
            props.setProperty("api.key", apiKey);

            try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
                props.store(out, "AI Configuration");
                JOptionPane.showMessageDialog(dialog, "配置已保存！");
                dialog.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "保存失败：" + ex.getMessage());
            }
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainAppWithAIConfig().setVisible(true);
        });
    }
}
