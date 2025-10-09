package com.catalogservice.mappers;

import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.dto.auth.RegisterResponseDto;
import com.catalogservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public RegisterResponseDto mapToRegisterResponseDto(User user) {
        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setId(user.getId());
        registerResponseDto.setEmail(user.getEmail());
        registerResponseDto.setUsername(user.getUsername());
        registerResponseDto.setCreatedAt(user.getCreatedAt());
        return registerResponseDto;
    }
}
