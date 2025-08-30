package com.catalogservice;

import com.catalogservice.dto.ProductCreateRequestDto;
import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.entity.Product;
import com.catalogservice.mappers.ProductMapper;
import com.catalogservice.service.ProductService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<?> getProduct(@RequestParam Long id) {
        ProductResponseDto obtainedProduct = productService.getById(id);
        return ResponseEntity.ok(obtainedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
