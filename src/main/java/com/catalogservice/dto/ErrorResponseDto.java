package com.catalogservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class ErrorResponseDto {
    private String requestId;
    private String code;
    private Instant timestamp;
    private String message;
    private Map<String, List<String>> details;
    private String path;
}
