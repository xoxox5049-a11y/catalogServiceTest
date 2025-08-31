package com.catalogservice.controller;

import com.catalogservice.dto.ProductCreateRequestDto;
import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.dto.ProductUpdateRequestDto;
import com.catalogservice.exceptions.DuplicateProductException;
import com.catalogservice.mappers.ProductMapper;
import com.catalogservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequestDto dto) {
        ProductResponseDto createdProduct = productService.createProduct(dto);
        return ResponseEntity.created(URI.create("/api/v1/products/" + createdProduct.getId())).body(createdProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        ProductResponseDto obtainedProduct = productService.getById(id);
        return ResponseEntity.ok(obtainedProduct);
    }

    @GetMapping
    public ResponseEntity<?> getProducts(Pageable  pageable) {
       Page<ProductResponseDto> obtainedProducts = productService.getAll(pageable);
       return ResponseEntity.ok(obtainedProducts);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getProductsSearchByName(String query, Pageable  pageable) {
        Page<ProductResponseDto> obtainedProducts = productService.searchByName(query, pageable);
        return ResponseEntity.ok(obtainedProducts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequestDto dto) {
        ProductResponseDto productResponseDto = productService.updateProduct(id, dto);
        return ResponseEntity.ok(productResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
