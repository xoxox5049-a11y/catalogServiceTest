package com.catalogservice.dto.auth;

import lombok.Getter;

import java.time.LocalDate;

public class RegisterResponseDto {
    @Getter
    private Long id;
    @Getter
    private String email;
    @Getter
    private String username;
    private String password;
    @Getter
    private LocalDate createdAt;
}
