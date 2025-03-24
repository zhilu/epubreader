package com.ereader.view;

import com.ereader.Constants;
import com.ereader.model.MyBook;
import com.ereader.model.Bookshelf;
import com.ereader.service.BookshelfService;
import com.ereader.service.EpubBookService;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.List;

public class BookShelfBoardPanel extends JPanel {

    private EpubBookService epubBookService = new EpubBookService();

    private static final String default_cover = "/images/book.png";
    private List<MyBook> books;
    private Bookshelf item;
    private int currentPage = 0;
    private final int booksPerPage = 20;



    public BookShelfBoardPanel(){
        setLayout(new GridLayout(4, 5, 10, 10));
    }

    public void load(Bookshelf bookshelf) {
        this.item = bookshelf;
        this.books = bookshelf.getBooks();
        setCurrentPage(0);
    }

    public void init(Bookshelf bookshelf) {
        this.item = bookshelf;
        this.books = BookshelfService.INSTANCE.getBooksByBookshelf(bookshelf.getId());
        setCurrentPage(0);
    }


    public void setCurrentPage(int page) {
        this.currentPage = page;
        repaint();
        updateGrid();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制书架层
        g2d.setColor(Constants.color);
        for (int i = 1; i <= 3; i++) {
            int y = (getHeight() / 4) * i;
            g2d.fillRect(0, y - 5, getWidth(), 10);
        }

        // 绘制底层架子，颜色稍浅
        g2d.setColor(new Color(160, 82, 45));
        g2d.fillRect(0, getHeight() - 10, getWidth(), 8);

        // 绘制虚线分隔书
        g2d.setColor(new Color(200, 200, 200));
        BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
        g2d.setStroke(dashed);
        int rowHeight = getHeight() / 4;
        int colWidth = getWidth() / 5;

        for (int i = 1; i < 5; i++) { // 竖线
            int x = colWidth * i;
            g2d.drawLine(x, 0, x, getHeight());
        }

        for (int i = 1; i < 4; i++) { // 横线
            int y = rowHeight * i;
            g2d.drawLine(0, y, getWidth(), y);
        }
    }

    public void updateGrid() {
        removeAll();
        int start = currentPage * booksPerPage;
        int end = Math.min(start + booksPerPage, books.size());

        for (int i = start; i < end; i++) {
            MyBook book = books.get(i);
            Book epubBook = epubBookService.resole(book.getFilePath());
            Resource coverImageResource = epubBook.getCoverImage();
            Image image = null;
            try {
                image = ImageIO.read(coverImageResource.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageIcon icon = new ImageIcon(image.getScaledInstance(100, 150, Image.SCALE_SMOOTH));
            JButton bookButton = new JButton(icon);
            bookButton.setToolTipText(book.getBookName() + " by " + book.getAuthor());


            bookButton.addActionListener(e -> {
                onBookClicked(epubBook);
            });

            
            add(bookButton);
        }

        for (int i = end; i < start + booksPerPage; i++) {
            JLabel emptySlot = new JLabel("添加");
            emptySlot.setHorizontalAlignment(JLabel.CENTER);
            add(emptySlot);
        }

        revalidate();
        repaint();
    }

    private void onBookClicked(Book book) {
        ReadingPanel readingPanel = ReadingPanel.getInstance();
        readingPanel.setBook(book);
        AppFrame.getInstance().switchToPanel(readingPanel);
    }
}
