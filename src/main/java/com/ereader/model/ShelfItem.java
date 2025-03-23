package com.ereader.model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
public class ShelfItem {

    private String name;
    private String icon;
    private List<MyBook> books = new ArrayList<>();

    public ShelfItem(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<MyBook> getBooks() {
        return books;
    }

    public void addBook(File file) {
        MyBook myBook = new MyBook(file.getName());
        myBook.setFilePath(file.getAbsolutePath());
        myBook.setFileName(file.getName());
        books.add(myBook);
    }
}
