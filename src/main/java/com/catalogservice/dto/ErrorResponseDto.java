package com.catalogservice.dto;

import java.time.Instant;
import java.util.Map;

public class ErrorResponseDto {
    private Instant timestamp;
    private String message;
    private Map<String, String> details;
    private String path;
}
