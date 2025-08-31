package com.catalogservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
public class ErrorResponseDto {
    private Instant timestamp;
    private String message;
    private Map<String, String> details;
    private String path;
}
