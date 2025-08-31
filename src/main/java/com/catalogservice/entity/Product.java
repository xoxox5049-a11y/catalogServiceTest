package com.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Table(name = "product", indexes = {@Index(columnList = "name"), @Index(columnList = "sku")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Setter
    @Column(length = 1000)
    private String description;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(nullable= false)
    private Integer stock;
    @Column(nullable= false)
    private Instant createdAt;
    @Column(nullable= false)
    private Instant updatedAt;
    @Version
    private Long version;
    @Column(nullable= false, length = 32, unique = true)
    private String sku;

    public Product(String name, String description, BigDecimal price, Integer stock, String sku) {
        if(Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is null or empty");
        } else {
            this.name = name.trim();
        }
        if(price == null) {
            throw new IllegalArgumentException("Price must be not null");
        } else if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        } else {
            this.price = price;
        }
        if(stock == null){
            throw new IllegalArgumentException("Stock must be not null");
        } else if(stock < 0) {
            throw new IllegalArgumentException("Stock must be greater than 0");
        }
        else {
            this.stock = stock;
        }
        if(Objects.isNull(sku) || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU must be not null");
        } else {
            this.sku = sku.trim().toUpperCase();
        }
        this.description = description;
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public void setName(String name) {
        if(Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is null or empty");
        } else {
            this.name = name.trim();
        }
    }

    public void setPrice(BigDecimal price) {
        if(price == null) {
            throw new IllegalArgumentException("Price must be not null");
        } else if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        } else {
            this.price = price;
        }
    }

    public void setStock(Integer stock) {
        if(stock == null){
            throw new IllegalArgumentException("Stock must be not null");
        } else if(stock < 0) {
            throw new IllegalArgumentException("Stock must be greater than 0");
        }
        else {
            this.stock = stock;
        }
    }

}
