package com.catalogservice.service.auth;

import com.catalogservice.dto.auth.LoginRequestDto;
import com.catalogservice.dto.auth.LoginResponseDto;
import com.catalogservice.dto.auth.RegisterRequestDto;
import com.catalogservice.dto.auth.RegisterResponseDto;
import com.catalogservice.entity.Role;
import com.catalogservice.entity.User;
import com.catalogservice.exceptions.AccountDisabledException;
import com.catalogservice.exceptions.DuplicateEmailException;
import com.catalogservice.exceptions.DuplicateUsernameException;
import com.catalogservice.exceptions.InvalidCredentialsException;
import com.catalogservice.mappers.UserMapper;
import com.catalogservice.repository.RoleRepository;
import com.catalogservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        String email = registerRequestDto.getEmail().trim().toLowerCase();
        String username = registerRequestDto.getUsername().trim();
        if(userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("Email already exists");
        }
        if(userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateUsernameException("Username already exists");
        }
        Optional<Role> roleUser = roleRepository.findByName("ROLE_USER");
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setEnabled(true);
        user.addRole(roleUser.orElseThrow(()->new IllegalStateException("Missing seed role ROLE_USER")));
        return userMapper.mapToRegisterResponseDto(userRepository.save(user));
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String requestEmail = loginRequestDto.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(requestEmail)
                .orElseThrow(() -> {
                    log.warn("LOGIN_FAIL reason=INVALID_CREDENTIALS email={}", maskEmail(requestEmail));
                    return new InvalidCredentialsException("Invalid credentials");
                });

        if(Boolean.FALSE.equals(user.getIsEnabled())) {
            log.warn("LOGIN_FAIL reason=ACCOUNT_DISABLED email={}", maskEmail(requestEmail));
            throw new AccountDisabledException("Account disabled");
        }
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPasswordHash())) {
            log.warn("LOGIN_FAIL reason=INVALID_CREDENTIALS email={}", maskEmail(requestEmail));
            throw new InvalidCredentialsException("Invalid credentials");
        }
        var roleNames = user.getRoles().stream().map(Role::getName).sorted().toList();
        log.info("LOGIN_SUCCESS userId={} email={} roles={}", user.getId(), maskEmail(requestEmail), roleNames);

        return LoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .roles(roleNames)
                .build();
    }

    private String maskEmail(String email) {
        if(email.isEmpty()) {
            return "<empty>";
        }
        int index = email.indexOf('@');
        if(index < 0) {
            return maskPlain(email);
        }
        String local = email.substring(0, index);
        String domain = email.substring(index + 1);
        String maskedLocal = maskPlain(local);
        return maskedLocal + "@" + domain;
    }
    private String maskPlain(String s) {
        if(s.length() <= 2) return s;
        String head = s.substring(0, 2);
        String tailMasked = "*".repeat(s.length() - 2);
        return head + tailMasked;
    }
}
