package com.ereader.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyBook {

    private String bookName;

    private String coverImage;
    private String path;
    private String author;
    private String fileName;
    private String filePath;

    public MyBook(String name) {
        this.bookName = name;
    }

    @Override
    public String toString() {
        return this.bookName;
    }
}
