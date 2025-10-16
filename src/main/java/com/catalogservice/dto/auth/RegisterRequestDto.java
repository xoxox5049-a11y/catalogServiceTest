package com.catalogservice.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RegisterRequestDto {
    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
    @NotBlank
    @Size(min=3, max=32)
    private String username;
    @NotBlank
    @Size(min=8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
