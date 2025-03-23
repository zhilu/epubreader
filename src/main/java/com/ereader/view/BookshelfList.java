package com.ereader.view;

import com.ereader.Constants;
import com.ereader.model.ShelfItem;
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

public class BookshelfList extends JPanel {

    public BookshelfService bookshelfService = new BookshelfService();

    private static final String default_icon = "/images/books.png";
    private static final float ICON_FACTOR = 1.5F;

    private JScrollPane pane;
    DefaultListModel<ShelfItem> listModel;
    JList<ShelfItem> list;
    BookShelfBoard bookShelfBoard;


    public BookshelfList(BookShelfBoard bookShelfBoard){
        setLayout(new BorderLayout());
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
                ShelfItem selectedItem = listModel.getElementAt(index);
                if (e.getClickCount() == 2) {

                    String newName = JOptionPane.showInputDialog("修改书架名称:", selectedItem.getName());
                    if (newName != null && !newName.trim().isEmpty()) {
                        selectedItem.setName(newName);
                        listModel.setElementAt(selectedItem, index);
                    }
                }else if(e.getClickCount() ==1){
                    bookShelfBoard.load(selectedItem);
                }
            }
        });

        JButton addButton = new JButton("添加");
        JButton removeButton = new JButton("删除");


        addButton.addActionListener(e -> {
            String newItem = JOptionPane.showInputDialog("输入新项目:");
            if (newItem != null && !newItem.trim().isEmpty()) {
                listModel.addElement(new ShelfItem(newItem));
                list.ensureIndexIsVisible(listModel.size() - 1);
            }
        });

        removeButton.addActionListener(e -> {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex);
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
            bookShelfBoard.load(listModel.elementAt(0));
        }
    }

    public void load(){
        List<ShelfItem> items = bookshelfService.loadBookshelf();
        listModel.clear();  // 清空旧数据
        for (ShelfItem item : items) {
            listModel.addElement(item);  // 正确添加数据
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
        ShelfItem shelfItem = list.getSelectedValue();
        shelfItem.addBook(file);
        bookShelfBoard.load(listModel.elementAt(selectedIndex));
    }


    private static class ShelfItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof ShelfItem) {
                ShelfItem item = (ShelfItem) value;
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
