package com.webscrappingpractice.webscrappingpractice.dto;


import lombok.Data;

@Data
public class JobNewsDto {
    private String imageURL;
    private String title;
    private String description;


    public JobNewsDto(String imageURL, String title, String description) {
        this.imageURL = imageURL;
        this.title = title;
        this.description = description;
    }
}
