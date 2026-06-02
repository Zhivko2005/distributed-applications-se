package com.freelance.freelance_api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleChangeDto {
    @NotBlank(message = "Role name cannot be empty")
    private String roleName;
}
