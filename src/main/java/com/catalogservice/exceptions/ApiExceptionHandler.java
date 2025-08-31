package com.catalogservice.exceptions;

import com.catalogservice.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException e, HttpServletRequest httpServletRequest) {
        return generateErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), getPath(httpServletRequest), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest httpServletRequest) {
        return generateErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), getPath(httpServletRequest), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                                  HttpServletRequest httpServletRequest) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> details = new HashMap<>();
        for(FieldError fieldError : fieldErrors) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return generateErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), getPath(httpServletRequest),details);
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateProductException(DuplicateProductException e, HttpServletRequest httpServletRequest) {
        return generateErrorResponse(HttpStatus.CONFLICT, e.getMessage(), getPath(httpServletRequest), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e, HttpServletRequest httpServletRequest) {
        return generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), getPath(httpServletRequest), null);
    }

    private ResponseEntity<ErrorResponseDto> generateErrorResponse(HttpStatus status, String message, String path, Map<String, String> details) {
        return ResponseEntity.status(status).body(ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .path(path)
                .details(details)
                .message(message).build());
    }

    private String getPath(HttpServletRequest httpServletRequest) {
        String url = httpServletRequest.getRequestURL().toString();
        String queryString = httpServletRequest.getQueryString();
        return (queryString != null && !queryString.isBlank()) ? url + "?" + queryString : url;
    }
}
