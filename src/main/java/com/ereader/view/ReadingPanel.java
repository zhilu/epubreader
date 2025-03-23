package com.ereader.view;

import com.ereader.service.EpubBookService;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

public class ReadingPanel extends JPanel {

    private static final float percent= 0.18F;

    private Book book;
    private EpubBookService epubBookService;
    private JSplitPane splitPane;
    private ReadingTOCPanel toc;
    private ReadingContentPanel contentPanel;
    private Navigator navigator = new Navigator();

    public ReadingPanel(Book book) {
        setLayout(new BorderLayout());
        JButton backButton = new JButton("返回书架");
        backButton.addActionListener(e -> backToShelf());
        add(backButton, BorderLayout.NORTH);

        this.book = book;
        this.epubBookService = new EpubBookService();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        toc = new ReadingTOCPanel(navigator);
        contentPanel = new ReadingContentPanel(navigator);
        splitPane.setLeftComponent(toc);
        splitPane.setResizeWeight(percent);
        splitPane.setRightComponent(contentPanel);
        navigator.gotoBook(book, this);

        add(splitPane, BorderLayout.CENTER);


        navigator.gotoBook(book, this);
        navigator.gotoFirstSpineSection(this);

    }



    private void backToShelf() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        BookshelfPanel shelfBoard = BookshelfPanel.instance;
        currentFrame.setContentPane(shelfBoard);
        currentFrame.revalidate();
        currentFrame.repaint();
    }
}
