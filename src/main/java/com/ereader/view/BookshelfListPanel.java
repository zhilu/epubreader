package com.ereader.view;

import com.ereader.Constants;
import com.ereader.model.Bookshelf;
import com.ereader.model.MyBook;
import com.ereader.service.BookshelfService;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class BookshelfListPanel extends JPanel {

    public BookshelfService bookshelfService = BookshelfService.INSTANCE;

    private static final String default_icon = "/images/books.png";
    private static final float ICON_FACTOR = 1.5F;

    private JScrollPane pane;
    DefaultListModel<Bookshelf> listModel;
    JList<Bookshelf> list;
    BookShelfBoardPanel bookShelfBoard;


    public BookshelfListPanel(BookShelfBoardPanel bookShelfBoard){
        setLayout(new BorderLayout());
        bookshelfService.init();
        this.bookShelfBoard = bookShelfBoard;

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setCellRenderer(new ShelfItemRenderer());
        pane = new JScrollPane(list);
        add(pane, BorderLayout.CENTER);




        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if(index == -1){
                    return;
                }
                Bookshelf bookshelf = listModel.getElementAt(index);
                if (e.getClickCount() == 2) {

                    String newName = JOptionPane.showInputDialog("修改书架名称:", bookshelf.getName());
                    if (newName != null && !newName.trim().isEmpty()) {
                        bookshelf.setName(newName);
                        listModel.setElementAt(bookshelf, index);
                        bookshelfService.updateBookshelf(bookshelf);
                    }
                }else if(e.getClickCount() ==1){
                    bookShelfBoard.init(bookshelf);
                }
            }
        });

        JButton addButton = new JButton("添加");
        JButton removeButton = new JButton("删除");


        addButton.addActionListener(e -> {
            String shelfName = JOptionPane.showInputDialog("创建新书架:");
            if (shelfName != null && !shelfName.trim().isEmpty()) {
                Bookshelf bookshelf = new Bookshelf();
                bookshelf.setName(shelfName);
                bookshelfService.addBookshelf(bookshelf);
                listModel.addElement(bookshelf);
                list.ensureIndexIsVisible(listModel.size() - 1);

            }
        });

        removeButton.addActionListener(e -> {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex != -1) {
                Bookshelf bookshelf = listModel.remove(selectedIndex);
                bookshelfService.deleteBookshelf(bookshelf.getId());
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一个要删除的项目");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        load();
        if(!listModel.isEmpty()){
            bookShelfBoard.init(listModel.elementAt(0));
        }
    }

    public void load(){
        List<Bookshelf> items = bookshelfService.listBookshelf();
        listModel.clear();
        for (Bookshelf item : items) {
            listModel.addElement(item);
        }
    }



    public void onOpenFile(File file) {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex == -1 && listModel.getSize() > 0) {
            list.setSelectedIndex(0);
            selectedIndex = 0;
        }
        if(selectedIndex == -1){
            JOptionPane.showMessageDialog(null, "没有书架");
            return;
        }
        Bookshelf shelfItem = list.getSelectedValue();
        MyBook myBook = new MyBook(file.getName());
        myBook.setFilePath(file.getAbsolutePath());
        myBook.setFileName(file.getName());
        bookshelfService.addBook(shelfItem.getId(),myBook);
        shelfItem.addBook(myBook);
        bookShelfBoard.load(listModel.elementAt(selectedIndex));
    }


    private static class ShelfItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Bookshelf) {
                Bookshelf item = (Bookshelf) value;
                label.setText(item.getName());
                String icon = item.getIcon();
                if(null == icon){
                    icon = default_icon;
                }

                int iconSize = (int) (Constants.FONT_SIZE * ICON_FACTOR);


                ImageIcon imageIcon = new ImageIcon(getClass().getResource(icon));
                Image img = imageIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(img));
                label.setHorizontalTextPosition(SwingConstants.RIGHT);
                label.setVerticalTextPosition(SwingConstants.CENTER);
                label.setFont(new Font(Constants.FONT_NAME, Font.PLAIN, Constants.FONT_SIZE));
            }

            return label;
        }
    }



}
