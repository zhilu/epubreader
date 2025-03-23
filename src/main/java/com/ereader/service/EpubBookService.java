package com.ereader.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class EpubBookService {

    public EpubReader epubReader = new EpubReader();

    public Book resole(String path){
        Book book = null;
        try {
            book = epubReader.readEpub(new FileInputStream(path));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return book;
    }
}
