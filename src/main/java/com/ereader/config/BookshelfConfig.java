package com.ereader.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookshelfConfig {

    public static BookshelfConfig instance = new BookshelfConfig();

    private static final float ICON_FACTOR = 1.5F;

    private String fontName =  "微软雅黑";
    private int fontSize = 40;
    private float iconSize = fontSize * ICON_FACTOR;
}
