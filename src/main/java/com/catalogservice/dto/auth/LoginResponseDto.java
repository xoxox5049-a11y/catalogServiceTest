package com.catalogservice.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class LoginResponseDto {
    private Long id;
    private String email;
    private String username;
    private Instant createdAt;
    private List<String> roles;
}
