package com.catalogservice.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RegisterResponseDto {
    private Long id;
    private String email;
    private String username;
    private Instant createdAt;
}
