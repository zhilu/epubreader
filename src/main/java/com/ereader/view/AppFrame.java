package com.ereader.view;

import nl.siegmann.epublib.epub.EpubReader;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import static com.ereader.Constants.APP_NAME;

public class AppFrame extends JFrame{

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;


    public AppFrame(){
        setTitle(APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);

        BookshelfPanel panel =  BookshelfPanel.instance;

        setJMenuBar(panel.create());

        setContentPane(panel);

        setLocationRelativeTo(null);

        setVisible(true);
    }


}
