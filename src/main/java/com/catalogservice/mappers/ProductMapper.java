package com.catalogservice.mappers;

import com.catalogservice.dto.ProductCreateRequestDto;
import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponseDto mapToProductResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .sku(product.getSku())
                .build();
    }

    public Product mapToProduct(ProductCreateRequestDto productCreateRequestDto) {
        return new Product(productCreateRequestDto.getName(),
                productCreateRequestDto.getDescription(),
                productCreateRequestDto.getPrice(),
                productCreateRequestDto.getStock(),
                productCreateRequestDto.getSku());
    }
}
