package com.catalogservice.dto.auth;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private Long id;
    private String email;
    private String username;
    private String status;
}
