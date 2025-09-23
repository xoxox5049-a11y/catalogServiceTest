package com.catalogservice.dto.auth;

import lombok.Getter;

public class LoginRequestDto {
    @Getter
    private String email;
    private String password;
}
