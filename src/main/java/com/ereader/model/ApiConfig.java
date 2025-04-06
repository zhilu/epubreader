package com.ereader.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiConfig {

    private String endpoint;
    private String key;
    private String model;
    private String template;
}
