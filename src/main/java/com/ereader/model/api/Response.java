package com.ereader.model.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Response {
    List<Choose> choices;
}
