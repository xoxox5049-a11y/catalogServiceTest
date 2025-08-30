package com.catalogservice.service;

import com.catalogservice.dto.ProductCreateRequestDto;
import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.dto.ProductUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
     ProductResponseDto createProduct(ProductCreateRequestDto productCreateRequestDto);
    ProductResponseDto getById(Long id);
    Page<ProductResponseDto> getAll(Pageable pageable);
    Page<ProductResponseDto> searchByName(String query, Pageable pageable);
    ProductResponseDto updateProduct(Long id, ProductUpdateRequestDto productUpdateRequestDto);
    void deleteProduct(Long id);

}
