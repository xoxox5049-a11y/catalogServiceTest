package com.catalogservice;

import com.catalogservice.entity.Product;
import com.catalogservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@DataJpaTest
public class ProductRepoTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testSaveAndRead() {
        BigDecimal price = new BigDecimal("100");
        Product product = new Product("product1", "descProduct1", price, 1, "1");
        Product save = productRepository.saveAndFlush(product);

        Product product1 = productRepository.findById(save.getId()).orElse(null);
        assertThat(product1).isNotNull();
        assertThat(product1.getId()).isNotNull();

        assertThat(product1.getCreatedAt()).isNotNull();
        assertThat(product1.getUpdatedAt()).isNotNull();
        assertThat(product1.getUpdatedAt()).isAfterOrEqualTo(product1.getCreatedAt());
    }

    @Test
    public void existsByNameIgnoreCase() {
        BigDecimal price = new BigDecimal("100");
        Product productToSave2 = new Product("Product1", "descProduct1", price, 1, "1");
        Product save2 = productRepository.saveAndFlush(productToSave2);

        Product product2 = productRepository.findById(save2.getId()).orElse(null);

        assertThat(product2).isNotNull();
        assertThat(product2.getId()).isNotNull();

        boolean a = productRepository.existsByNameIgnoreCase("product1");
        boolean b = productRepository.existsByNameIgnoreCase("product2");
        assertThat(a).isTrue();
        assertThat(b).isFalse();
    }

    @Test
    public void checkPaginationAndSort() {
        BigDecimal pric1 = new BigDecimal("100");
        BigDecimal pric2 = new BigDecimal("200");
        BigDecimal pric3 = new BigDecimal("300");
        Product product1 = new Product("Aproduct1", "descProduct1", pric1, 1, "1");
        Product product2 = new Product("Bproduct2", "descProduct2", pric2, 2, "2");
        Product product3 = new Product("Cproduct3", "descProduct3", pric3, 3, "3");
        List<Product> products =List.of(product1, product2, product3);
        productRepository.saveAll(products);
        productRepository.flush();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());
        Page<Product> all = productRepository.findAll(pageable);

        assertThat(all).isNotNull();
        assertThat(all.getContent()).hasSize(2);
        assertThat(all.getTotalElements()).isEqualTo(3);
        assertThat(all.getContent()).extracting(Product::getName).containsExactly("Aproduct1", "Bproduct2");
    }

    @Test
    public void checkUpdate() throws InterruptedException {
        BigDecimal price = new BigDecimal("100");
        Product productToSave = new Product("Product1", "descProduct1", price, 1, "1");
        Product save2 = productRepository.saveAndFlush(productToSave);

        Product product1 = productRepository.findById(save2.getId()).orElse(null);

        assertThat(product1).isNotNull();
        assertThat(product1.getId()).isNotNull();

        Instant createdAt1 = product1.getCreatedAt();
        Instant updatedAt1 = product1.getUpdatedAt();

        productToSave.setStock(2);
        Thread.sleep(10);
        Product newProductStock = productRepository.saveAndFlush(productToSave);

        Product product2 = productRepository.findById(newProductStock.getId()).orElse(null);

        assertThat(product2).isNotNull();
        assertThat(product2.getId()).isNotNull();

        Instant createdAt2 = product2.getCreatedAt();
        Instant updatedAt2 = product2.getUpdatedAt();

        assertThat(updatedAt2).isAfterOrEqualTo(updatedAt1);
        assertThat(createdAt1).isEqualTo(createdAt2);
        assertThat(product2.getStock()).isEqualTo(2);
    }
}
