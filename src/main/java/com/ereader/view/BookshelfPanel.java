package com.ereader.view;

import com.ereader.App;
import com.ereader.model.ReadingMode;
import lombok.Getter;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.io.File;
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
        JMenu viewMenu = new JMenu("View");
        JMenu aboutMenu = new JMenu("About");

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openFile());

        JMenuItem importItem = new JMenuItem("Import");
        importItem.addActionListener(e -> importFiles());

        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> openSettings());

        fileMenu.add(openItem);
        fileMenu.add(importItem);
        fileMenu.add(settingsItem);

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
}
