package com.ereader.model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
public class Bookshelf {

    private Integer id;
    private String name;
    private String icon;
    private List<MyBook> books = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }

    public List<MyBook> getBooks() {
        return books;
    }

    public void addBook(MyBook myBook) {
        books.add(myBook);
    }
}
