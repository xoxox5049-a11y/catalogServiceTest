package com.catalogservice.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class RegisterRequestDto {
    @Getter
    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
    @Getter
    @NotBlank
    @Size(min=3, max=32)
    private String username;
    @NotBlank
    @Size(min=8)
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
