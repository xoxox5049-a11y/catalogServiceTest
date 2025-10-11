package com.catalogservice.service.auth;

import com.catalogservice.dto.auth.RegisterRequestDto;
import com.catalogservice.dto.auth.RegisterResponseDto;
import com.catalogservice.entity.Role;
import com.catalogservice.entity.User;
import com.catalogservice.exceptions.DuplicateEmailException;
import com.catalogservice.exceptions.DuplicateUsernameException;
import com.catalogservice.mappers.UserMapper;
import com.catalogservice.repository.RoleRepository;
import com.catalogservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        if(userRepository.existsByEmailIgnoreCase(registerRequestDto.getEmail().trim().toLowerCase())) {
            throw new DuplicateEmailException("Email already exists");
        }
        if(userRepository.existsByUsernameIgnoreCase(registerRequestDto.getUsername().trim())) {
            throw new DuplicateUsernameException("Username already exists");
        }
        Optional<Role> roleUser = roleRepository.findByName("ROLE_USER");
        User user = new User();
        user.setUsername(registerRequestDto.getUsername());
        user.setEmail(registerRequestDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setEnabled(true);
        user.addRole(roleUser.orElseThrow(()->new IllegalStateException("Missing seed role ROLE_USER")));
        return userMapper.mapToRegisterResponseDto(userRepository.save(user));
    }
}
