package com.ereader.view;

import nl.siegmann.epublib.epub.EpubReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import java.awt.BorderLayout;

import static com.ereader.Constants.APP_NAME;

public class AppFrame extends JFrame{

    public static AppFrame INSTANCE = null;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private JPanel contentPanel;


    public static AppFrame getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppFrame();
        }
        return INSTANCE;
    }

    private AppFrame(){
        setTitle(APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);

        BookshelfPanel panel =  BookshelfPanel.instance;
        setJMenuBar(panel.create());
        contentPanel = panel;

        switchToPanel(panel);

        JToolBar toolBar = createToolBar();
        getContentPane().add(toolBar, BorderLayout.NORTH);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton returnButton = new JButton("返回");
        returnButton.addActionListener(e->
                AppFrame.getInstance().switchToPanel(BookshelfPanel.instance)
        );
        toolBar.add(returnButton);
        return toolBar;
    }


    public void switchToPanel(JPanel panel) {

        remove(contentPanel);
        contentPanel = panel;
        add(contentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }


}
