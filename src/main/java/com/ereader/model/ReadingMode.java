package com.ereader.model;

import lombok.Getter;

import java.awt.Color;

@Getter
public enum ReadingMode {
    DARK_BLUE(new Color(65,80,98),new Color(255,246,230)),
    DARK_GREEN(new Color(65,68,65),new Color(213,206,205)),
    WHITE(new Color(255, 255, 255), new Color(65,68,65)),
    YELLOW(new Color(250, 249, 222), new Color(65,68,65)),
    GREEN(new Color(227, 237, 205), new Color(65,68,65)),
    ;

    private Color background = null;
    private Color foreground = null;

    ReadingMode(Color background, Color foreground) {
        this.background = background;
        this.foreground = foreground;

    }
}
