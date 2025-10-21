package com.tudor.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserDisplayDTO {
    private Integer userId;
    private String fullName;
    private String imagePath;
}
