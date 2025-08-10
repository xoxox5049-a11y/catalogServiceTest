package com.catalogservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public abstract class BaseDto {
    @NotBlank
    @Size(min= 2, max=120)
    private String name;
    @Size(max=1000)
    private String description;
    @NotBlank
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal price;
    @NotNull
    @Min(0)
    private Integer stock;

}
