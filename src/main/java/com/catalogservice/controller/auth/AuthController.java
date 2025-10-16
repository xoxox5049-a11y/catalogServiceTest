package com.catalogservice.controller.auth;

import com.catalogservice.dto.ErrorResponseDto;
import com.catalogservice.dto.auth.RegisterRequestDto;
import com.catalogservice.dto.auth.RegisterResponseDto;
import com.catalogservice.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "email, username уникален; в Location вернётся URI созданного ресурса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = RegisterResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (дубликат email или username)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto requestDto) {
        RegisterResponseDto register = authService.register(requestDto);
        return ResponseEntity.created(URI.create("/users/" + register.getId())).body(register);
    }
}
