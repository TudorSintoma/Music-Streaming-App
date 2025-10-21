package com.tudor.demo.dto;

import lombok.Data;

@Data
public class DiscoveryFilterDTO {
    private String searchQuery;
    private Integer genreId;
    private String yearCategory;
    private String popularity;
    private String orderBy;
}
