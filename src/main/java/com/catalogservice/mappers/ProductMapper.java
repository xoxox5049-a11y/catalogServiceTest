package com.catalogservice.mappers;

import com.catalogservice.dto.ProductCreateRequestDto;
import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponseDto mapToProductResponseDto(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setStock(product.getStock());
        productResponseDto.setCreatedAt(product.getCreatedAt());
        productResponseDto.setUpdatedAt(product.getUpdatedAt());
        productResponseDto.setSku(product.getSku());
        return productResponseDto;
    }

    public Product mapToProduct(ProductCreateRequestDto productCreateRequestDto) {
        return new Product(productCreateRequestDto.getName(),
                productCreateRequestDto.getDescription(),
                productCreateRequestDto.getPrice(),
                productCreateRequestDto.getStock(),
                productCreateRequestDto.getSku());
    }
}
