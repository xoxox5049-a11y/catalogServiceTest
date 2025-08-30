package com.catalogservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProductCreateRequestDto extends BaseDto{
    @NotBlank
    @Size(min=3, max=32)
    private String sku;
}
