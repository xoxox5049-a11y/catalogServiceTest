package com.catalogservice.controller;

import com.catalogservice.dto.ErrorResponseDto;
import com.catalogservice.dto.ProductCreateRequestDto;
import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.dto.ProductUpdateRequestDto;
import com.catalogservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Products", description = "Каталог товаров")
@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Создать продукт", description = "SKU уникален; в Location вернётся URI созданного ресурса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (дубликат SKU)",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid ProductCreateRequestDto dto) {
        ProductResponseDto createdProduct = productService.createProduct(dto);
        return ResponseEntity.created(URI.create("/api/v1/products/" + createdProduct.getId())).body(createdProduct);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

    })
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        ProductResponseDto obtainedProduct = productService.getById(id);
        return ResponseEntity.ok(obtainedProduct);
    }

    @GetMapping
    @Operation(summary = "Получить список продуктов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad Request лимит size/неверный sort",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

    })
    public ResponseEntity<Page<ProductResponseDto>> getProducts(@ParameterObject Pageable pageable) {
        if(pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("size must be <= 100");
        }
       Page<ProductResponseDto> obtainedProducts = productService.getAll(pageable);
       return ResponseEntity.ok(obtainedProducts);
    }

    @GetMapping("/search")
    @Operation(summary = "Получить список продуктов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad Request лимит size/неверный sort",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

    })
    public ResponseEntity<Page<ProductResponseDto>> getProductsSearchByName(@RequestParam("query") String query, @ParameterObject Pageable  pageable) {
        if(pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("size must be <= 100");
        }
        Page<ProductResponseDto> obtainedProducts = productService.searchByName(query, pageable);
        return ResponseEntity.ok(obtainedProducts);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить продукт по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",  content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

    })
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductUpdateRequestDto dto) {
        ProductResponseDto productResponseDto = productService.updateProduct(id, dto);
        return ResponseEntity.ok(productResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить продукт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),

    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
