package com.ereader.view;

import com.ereader.model.ReadingMode;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

@Slf4j
public class ReadingPanel extends JPanel {

    private static ReadingPanel INSTANCE = null;


    private static final float percent= 0.12F;
    private static final float SPLIT_META= 0.8F;

    private Book book;
    private JSplitPane splitPane;
    private ReadingTOCPanel tocPanel;
    private JSplitPane leftPanel;
    private ReadingContentPanel contentPanel;
    private Navigator navigator = new Navigator();
    private boolean showToc = true;

    public static ReadingPanel getInstance(){
        if(null == INSTANCE){
            INSTANCE = new ReadingPanel();
        }
        return INSTANCE;
    }

    public ReadingPanel() {
        setLayout(new BorderLayout());
    }

    public void setBook(Book book){
        this.book = book;
        this.navigator = new Navigator();
        initUI();
    }

    public void setReadingMode(ReadingMode readingMode){
        contentPanel.setReadingMode(readingMode);
    }

    private void initUI() {
        removeAll();

        // 创建分割面板
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        tocPanel = new ReadingTOCPanel(navigator);
        contentPanel = new ReadingContentPanel(navigator);

        leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        MetadataPanel metadataPane = new MetadataPanel(navigator);
        leftPanel.setTopComponent(tocPanel);
        leftPanel.setBottomComponent(metadataPane);
        leftPanel.setResizeWeight(SPLIT_META);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setResizeWeight(percent);
        splitPane.setRightComponent(contentPanel);

        add(splitPane, BorderLayout.CENTER);

        // 加载书籍
        navigator.gotoBook(book, this);
        navigator.gotoFirstSpineSection(this);

        revalidate();
        repaint();
    }

    public void changeTocPanel() {
        showToc = !showToc;
        if (showToc) {
            splitPane.setLeftComponent(leftPanel);
        } else {
            splitPane.remove(leftPanel);

        }
        splitPane.revalidate();
        splitPane.repaint();
    }
}
