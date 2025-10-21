package com.tudor.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name should be between 2 and 100 characters")
    private String fullName;


    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 100, message = "Password should be between 5 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{5,100}$",
            message = "Password must contain at least one digit and one special character"
    )
    private String password;
}
