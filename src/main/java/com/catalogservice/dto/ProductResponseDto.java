package com.catalogservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class ProductResponseDto{
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    @JsonFormat(shape =  JsonFormat.Shape.STRING)
    private Instant createdAt;
    @JsonFormat(shape =  JsonFormat.Shape.STRING)
    private Instant updatedAt;
}
