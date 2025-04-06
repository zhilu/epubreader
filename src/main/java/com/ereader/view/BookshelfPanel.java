package com.ereader.view;

import com.ereader.App;
import com.ereader.config.Constants;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;

import static com.ereader.model.ReadingMode.DARK_BLUE;
import static com.ereader.model.ReadingMode.DARK_GREEN;
import static com.ereader.model.ReadingMode.GREEN;
import static com.ereader.model.ReadingMode.WHITE;
import static com.ereader.model.ReadingMode.YELLOW;

public class BookshelfPanel extends JPanel {

    public static BookshelfPanel instance = new BookshelfPanel();


    public static final float shelves_percent = 0.15f;

    private static final String LAST_PATH_KEY = "history";
    private static Preferences preferences = Preferences.userNodeForPackage(App.class);


    private BookshelfListPanel shelves;
    private BookShelfBoardPanel board;

    private BookshelfPanel(){
        board = new BookShelfBoardPanel();
        shelves = new BookshelfListPanel(board);

        JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, shelves, board);
        main.setResizeWeight(shelves_percent);

        setLayout(new BorderLayout());

        add(main, BorderLayout.CENTER);
    }



    public JMenuBar create(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openFile());
        fileMenu.add(openItem);

        JMenuItem importItem = new JMenuItem("Import");
        importItem.addActionListener(e -> importFiles());
        fileMenu.add(importItem);

        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> openSettings());
        fileMenu.add(settingsItem);

        JMenuItem aiSetting = new JMenuItem("AI助手");
        aiSetting.addActionListener(e -> openAIDialog());
        fileMenu.add(aiSetting);




        JMenu viewMenu = new JMenu("View");
        JMenu aboutMenu = new JMenu("About");




        JMenuItem toggleViewItem = new JMenuItem("Toggle View");
        viewMenu.add(toggleViewItem);

        JMenu readMode = new JMenu("mode");
        JMenuItem darkMode = new JMenuItem("dark");
        JMenuItem dark2 = new JMenuItem("green");
        JMenuItem whiteMode = new JMenuItem("white");

        JMenuItem yellowMode = new JMenuItem("yellow");
        JMenuItem greenMode = new JMenuItem("green");

        readMode.add(whiteMode);
        readMode.add(darkMode);
        readMode.add(dark2);
        readMode.add(yellowMode);
        readMode.add(greenMode);
        viewMenu.add(readMode);

        darkMode.addActionListener(e -> ReadingPanel.getInstance().setReadingMode(DARK_BLUE));
        dark2.addActionListener(e -> ReadingPanel.getInstance().setReadingMode(DARK_GREEN));
        whiteMode.addActionListener(e -> ReadingPanel.getInstance().setReadingMode(WHITE));
        yellowMode.addActionListener(e -> ReadingPanel.getInstance().setReadingMode(YELLOW));
        greenMode.addActionListener(e -> ReadingPanel.getInstance().setReadingMode(GREEN));


        JMenuItem tocItem = new JMenuItem("TOC");
        viewMenu.add(tocItem);
        tocItem.addActionListener(e -> ReadingPanel.getInstance().changeTocPanel());



        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "BookshelfApp v1.0\nCreated by YourName"));
        aboutMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(aboutMenu);

        return menuBar;
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        String lastPath = preferences.get(LAST_PATH_KEY, null);
        if (lastPath != null) {
            File lastDir = new File(lastPath);
            if (lastDir.exists() && lastDir.isDirectory()) {
                fileChooser.setCurrentDirectory(lastDir); // 设置为上次的路径
            }
        }

        fileChooser.setFileFilter(new FileNameExtensionFilter("EPUB Files", "epub"));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // 调用打开文件的逻辑
            shelves.onOpenFile(file);
        }
    }

    private void openSettings() {

    }

    private void importFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("EPUB Files", "epub"));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                // 导入每个文件
                //bookShelfBoard.loadEpub(file);
            }
        }
    }


    private void openAIDialog() {
        JDialog dialog = new JDialog((Dialog) null,true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);

        // 创建组件
        JLabel apiEndpoint = new JLabel("接口地址：");
        JTextField endpointField = new JTextField(20);

        JLabel apiKeyLabel = new JLabel("使用密钥：");
        JPasswordField apiKeyField = new JPasswordField(20);

        JLabel apiModelLabel = new JLabel("使用模型：");
        JTextField apiModelLField = new JTextField(20);

        JLabel apiTemplateLabel = new JLabel("使用模板：");
        JTextArea apiTemplateArea = new JTextArea(5, 20);
        apiTemplateArea.setLineWrap(true);
        apiTemplateArea.setWrapStyleWord(true);
        int rowHeight = apiTemplateArea.getFontMetrics(apiTemplateArea.getFont()).getHeight();
        apiTemplateArea.setPreferredSize(new Dimension(300, rowHeight * 5));

        // 限制最大行数为 5 行
        PlainDocument doc = (PlainDocument) apiTemplateArea.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                // 获取当前文档内容并按行拆分
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                int currentLines = currentText.split("\n").length;

                // 限制最多 5 行，回车换行时判断行数
                if (currentLines < 5 || string.equals("\n")) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    Toolkit.getDefaultToolkit().beep(); // 超出行数限制时提醒
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                int currentLines = currentText.split("\n").length;

                if (currentLines < 5 || text.contains("\n")) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep(); // 超出行数限制时提醒
                }
            }
        });

        JButton saveButton = new JButton("保存配置");

        // 加载配置
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(Constants.CONFIG_PATH)) {
            props.load(in);
            endpointField.setText(props.getProperty("api.endpoint", ""));
            apiKeyField.setText(props.getProperty("api.key", ""));
            apiModelLField.setText(props.getProperty("api.model", ""));
            apiTemplateArea.setText(props.getProperty("api.template", ""));
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
                                .addComponent(apiEndpoint)
                                .addComponent(endpointField))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(apiKeyLabel)
                                .addComponent(apiKeyField))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(apiModelLabel)
                                .addComponent(apiModelLField))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(apiTemplateLabel)
                                .addComponent(apiTemplateArea))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(saveButton))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(apiEndpoint)
                                .addComponent(endpointField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(apiKeyLabel)
                                .addComponent(apiKeyField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(apiModelLabel)
                                .addComponent(apiModelLField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(apiTemplateLabel)
                                .addComponent(apiTemplateArea))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(saveButton))
        );

        saveButton.addActionListener(e -> {
            String endpoint = endpointField.getText();
            String apiKey = new String(apiKeyField.getPassword());
            String apiModel = apiModelLField.getText();
            String apiTemplate = apiTemplateArea.getText();

            props.setProperty("api.endpoint", endpoint);
            props.setProperty("api.key", apiKey);
            props.setProperty("api.model", apiModel);
            props.setProperty("api.template", apiTemplate);

            try (FileOutputStream out = new FileOutputStream(Constants.CONFIG_PATH)) {
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
}
