package com.catalogservice.service;

import com.catalogservice.dto.ProductCreateRequestDto;
import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.dto.ProductUpdateRequestDto;
import com.catalogservice.entity.Product;
import com.catalogservice.exceptions.NotFoundException;
import com.catalogservice.mappers.ProductMapper;
import com.catalogservice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@Transactional(readOnly=true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductCreateRequestDto productCreateRequestDto) {
        Product save = productRepository.save(mapper.mapToProduct(productCreateRequestDto));
        return mapper.mapToProductResponseDto(save);
    }

    @Override
    public ProductResponseDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Product with id: %s not found", id)));
        return mapper.mapToProductResponseDto(product);
    }

    @Override
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        validationSort(pageable.getSort());
        Page<Product> all = productRepository.findAll(pageable);
        return all.map(mapper::mapToProductResponseDto);
    }

    @Override
    public Page<ProductResponseDto> searchByName(String query, Pageable pageable) {
        if(query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query is null or empty");
        }
        query = query.trim();
        if(query.length() < 2 || query.length() > 120){
            throw new IllegalArgumentException("query length should be between 2 and 120");
        }
        validationSort(pageable.getSort());

        Page<Product> allByNameContainingIgnoreCase = productRepository.findAllByNameContainingIgnoreCase(query, pageable);
        return allByNameContainingIgnoreCase.map(mapper::mapToProductResponseDto);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductUpdateRequestDto productUpdateRequestDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Product with id: %s not found", id)));
        product.setName(productUpdateRequestDto.getName());
        product.setDescription(productUpdateRequestDto.getDescription());
        product.setPrice(productUpdateRequestDto.getPrice());
        product.setStock(productUpdateRequestDto.getStock());
        product = productRepository.save(product);
        return mapper.mapToProductResponseDto(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Product with id: %s not found", id)));
        productRepository.deleteById(id);
    }

    private void validationSort(Sort sort) {
        if(sort.isUnsorted()){
            return;
        }
        Set<String> validFields = Set.of("name", "price", "stock", "createdAt");
        sort.stream().filter(order -> !validFields.contains(order.getProperty())).forEach(order -> {
            throw new IllegalArgumentException(String.format("Invalid sort parameter: %s", order.getProperty()));
        });
    }
}
