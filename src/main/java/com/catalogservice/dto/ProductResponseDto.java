package com.catalogservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
    private String sku;
}
