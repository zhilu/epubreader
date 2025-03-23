package com.ereader.service;

import com.ereader.model.MyBook;
import com.ereader.model.ShelfItem;

import javax.swing.JList;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import java.util.List;

public class BookshelfService {

    public List<ShelfItem> loadBookshelf(){
        List<ShelfItem> items = new ArrayList<>();

        String[] names = {"默认", "常识","其他"};
        for (String name : names) {
            ShelfItem item = new ShelfItem(name);
            items.add(item);
        }

        return items;
    }


    public List<MyBook> loadBook(ShelfItem shelf){
        List<MyBook> items = new ArrayList<>();

        String[] names = {"a", "b","c"};
        for (String name : names) {
            MyBook item = new MyBook(name);
            items.add(item);
        }

        return items;
    }
}
