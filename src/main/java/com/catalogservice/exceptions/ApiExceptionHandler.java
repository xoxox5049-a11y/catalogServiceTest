package com.catalogservice.exceptions;

import com.catalogservice.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException e, HttpServletRequest httpServletRequest) {
        String path = getPath(httpServletRequest);
        String requestId = httpServletRequest.getHeader("X-Request-Id");
        log.info("404 CLIENT_ERROR_NOT_FOUND requestId={} path={} msg={}",
                requestId, path, e.getMessage());

        return generateErrorResponse(HttpStatus.NOT_FOUND, "Not Found",
                path, requestId, "NOT_FOUND", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest httpServletRequest) {
        String path = getPath(httpServletRequest);
        String requestId = httpServletRequest.getHeader("X-Request-Id");
        extractedBadRequestLog(e, requestId, path);

        return generateErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                path, requestId, "BAD_REQUEST", null);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                                  HttpServletRequest httpServletRequest) {
        String path = getPath(httpServletRequest);
        String requestId = httpServletRequest.getHeader("X-Request-Id");

        Map<String, List<String>> details = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            String field = fe.getField();
            if ("password".equalsIgnoreCase(field)) continue;
            details.computeIfAbsent(field, k -> new ArrayList<>())
                    .add(fe.getDefaultMessage());
        }

        extractedBadRequestLog(e, requestId, path);

        ErrorResponseDto body = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .requestId(httpServletRequest.getHeader("X-Request-Id"))
                .path(getPath(httpServletRequest))
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest httpServletRequest) {
        String path = getPath(httpServletRequest);
        String requestId = httpServletRequest.getHeader("X-Request-Id");
        extractedConflictLog(e, requestId, path);

        return generateErrorResponse(HttpStatus.CONFLICT, "Duplicate value",
                path, requestId, "UNIQUE_VIOLATION", null);
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateProductException(DuplicateProductException e, HttpServletRequest httpServletRequest) {
        String path = getPath(httpServletRequest);
        String requestId = httpServletRequest.getHeader("X-Request-Id");
        extractedConflictLog(e, requestId, path);

        return generateErrorResponse(HttpStatus.CONFLICT, "Duplicate value",
                path, requestId, "UNIQUE_VIOLATION", Map.of("sku", List.of("already exists")));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateEmailException(DuplicateEmailException e, HttpServletRequest httpServletRequest) {
        String path = getPath(httpServletRequest);
        String requestId = httpServletRequest.getHeader("X-Request-Id");
        extractedConflictLog(e, requestId, path);

        return generateErrorResponse(HttpStatus.CONFLICT, "Duplicate value",
                path, requestId, "UNIQUE_VIOLATION", Map.of("email", List.of("already exists")));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateUsernameException(DuplicateUsernameException e, HttpServletRequest httpServletRequest) {
        String path = getPath(httpServletRequest);
        String requestId = httpServletRequest.getHeader("X-Request-Id");
        extractedConflictLog(e, requestId, path);

        return generateErrorResponse(HttpStatus.CONFLICT, "Duplicate value",
                path, requestId, "UNIQUE_VIOLATION", Map.of("username", List.of("already exists")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e, HttpServletRequest httpServletRequest) {
        String path = getPath(httpServletRequest);
        String requestId = httpServletRequest.getHeader("X-Request-Id");
        log.error("500 INTERNAL_SERVER_ERROR requestId={} path={} msg={}",
                requestId, path, e.getMessage(), e);

        return generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                path, requestId, "INTERNAL_SERVER_ERROR", null);
    }

    private ResponseEntity<ErrorResponseDto> generateErrorResponse(HttpStatus status, String message, String path,
                                                                   String requestId, String code,
                                                                   Map<String, List<String>> details) {
        return ResponseEntity.status(status).body(ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .requestId(requestId)
                .code(code)
                .path(path)
                .details(details)
                .message(message).build());
    }

    private static void extractedConflictLog(Exception e, String requestId, String path) {
        log.warn("409 CLIENT_ERROR_CONFLICT requestId={} path={} msg={}",
                requestId, path, e.getMessage());
    }

    private static void extractedBadRequestLog(Exception e, String requestId, String path) {
        log.info("400 CLIENT_ERROR_BAD_REQUEST requestId={} path={} msg={}",
                requestId, path, e.getMessage());
    }

    private String getPath(HttpServletRequest httpServletRequest) {
        String url = httpServletRequest.getRequestURL().toString();
        String queryString = httpServletRequest.getQueryString();
        return (queryString != null && !queryString.isBlank()) ? url + "?" + queryString : url;
    }
}
