package com.tudor.demo.dto;

import com.tudor.demo.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListenerDTO {

    @NotNull(message = "User ID is required")
    private User user;
}