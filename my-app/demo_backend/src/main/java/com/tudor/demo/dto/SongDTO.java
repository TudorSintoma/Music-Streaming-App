package com.tudor.demo.dto;

import lombok.Data;

@Data
public class SongDTO {
    private Integer id;
    private String name;
    private Integer likes;
    private Integer albumId;
}
