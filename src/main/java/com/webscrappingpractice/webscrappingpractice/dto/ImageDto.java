package com.webscrappingpractice.webscrappingpractice.dto;

import lombok.Data;

@Data
public class ImageDto {
    private String src;
    private String alt;

    public ImageDto(String src, String alt) {
        this.src = src;
        this.alt = alt;
    }
}
