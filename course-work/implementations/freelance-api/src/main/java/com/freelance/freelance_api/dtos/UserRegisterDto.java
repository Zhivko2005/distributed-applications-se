package com.freelance.freelance_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRegisterDto {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 25, message = "Username must be between 3 and 25 characters")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Please, provide a valid email address")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String confirmPassword;
}
