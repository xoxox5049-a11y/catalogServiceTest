package com.catalogservice.dto.auth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class RegisterRequestDto {
    @Getter
    @NotBlank
    @Max(value = 254)
    private String email;
    @Getter
    @NotBlank
    @Size(min=3, max=32)
    private String username;
    @NotBlank
    @Max(value = 8)
    private String password;
}
