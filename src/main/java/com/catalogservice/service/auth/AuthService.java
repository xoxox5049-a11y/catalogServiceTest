package com.catalogservice.service.auth;

import com.catalogservice.dto.auth.LoginRequestDto;
import com.catalogservice.dto.auth.LoginResponseDto;
import com.catalogservice.dto.auth.RegisterRequestDto;
import com.catalogservice.dto.auth.RegisterResponseDto;
import org.springframework.stereotype.Component;

@Component
public interface AuthService {
    RegisterResponseDto register(RegisterRequestDto registerRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);
}
