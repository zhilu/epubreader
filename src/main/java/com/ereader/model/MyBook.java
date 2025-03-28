package com.ereader.model;

import lombok.Getter;
import lombok.Setter;
import nl.siegmann.epublib.domain.Book;

@Getter
@Setter
public class MyBook {

    private Integer id;
    private String bookName;

    private String coverImage;
    private String path;
    private String author;
    private String fileName;
    private String filePath;
    private Book book;

    @Override
    public String toString() {
        return this.bookName;
    }
}
