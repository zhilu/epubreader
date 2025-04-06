package com.ereader.model.api;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Choose {
    private Integer index;
    private Message message;
    private String finishReason;
}
