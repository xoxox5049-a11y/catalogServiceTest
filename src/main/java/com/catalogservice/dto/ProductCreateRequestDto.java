package com.catalogservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ProductCreateRequestDto extends BaseDto{
    @NotBlank
    @Size(min=3, max=32)
    @Setter
    private String sku;
}
