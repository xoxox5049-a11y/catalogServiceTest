package com.catalogservice;

import com.catalogservice.dto.ProductCreateRequestDto;
import com.catalogservice.dto.ProductResponseDto;
import com.catalogservice.dto.ProductUpdateRequestDto;
import com.catalogservice.entity.Product;
import com.catalogservice.exceptions.NotFoundException;
import com.catalogservice.mappers.ProductMapper;
import com.catalogservice.repository.ProductRepository;
import com.catalogservice.service.ProductServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;
    @InjectMocks
    ProductServiceImpl productServiceImpl;
    @Spy
    ProductMapper productMapper;

    @Test
    public void createTest() {
        ProductCreateRequestDto productCreateRequestDto = new ProductCreateRequestDto();
        productCreateRequestDto.setName("iphone 13");
        productCreateRequestDto.setDescription("Blue one");
        productCreateRequestDto.setPrice(new BigDecimal("999.99"));
        productCreateRequestDto.setStock(5);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(inv -> inv.getArgument(0, Product.class));

        ProductResponseDto productResponseDto = productServiceImpl.createProduct(productCreateRequestDto);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(captor.capture());
        Product product = captor.getValue();

        assertThat(product.getName()).isEqualTo("iphone 13");
        assertThat(product.getDescription()).isEqualTo("Blue one");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("999.99"));
        assertThat(product.getStock()).isEqualTo(5);

        assertThat(productResponseDto.getName()).isEqualTo("iphone 13");
        assertThat(productResponseDto.getDescription()).isEqualTo("Blue one");
        assertThat(productResponseDto.getPrice()).isEqualTo(new BigDecimal("999.99"));
        assertThat(productResponseDto.getStock()).isEqualTo(5);

        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void getById_happyCase() {
        Long id = 2213L;
        Product product = new Product("Product1", "desc", new BigDecimal("100.00"), 3, "1");
        ReflectionTestUtils.setField(product, "id", id);
        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        ProductResponseDto productResponseDto = productServiceImpl.getById(id);

        assertThat(productResponseDto.getId()).isEqualTo(id);
        assertThat(productResponseDto.getName()).isEqualTo("Product1");

        verify(productRepository, times(1)).findById(eq(id));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void getById_failTest() {
        Long id = 99L;

        when(productRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productServiceImpl.getById(id)).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, times(1)).findById(eq(id));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void deleteById_happyCase() {
        Long id = 2213L;
        Product product = new Product("Product1", "desc", new BigDecimal("100.00"), 3, "1");

        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        productServiceImpl.deleteProduct(id);

        InOrder inOrder = Mockito.inOrder(productRepository);

        inOrder.verify(productRepository).findById(eq(id));
        inOrder.verify(productRepository).deleteById(eq(id));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteById_failTest() {
        Long id = 99L;
        when(productRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productServiceImpl.deleteProduct(id)).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, times(1)).findById(eq(id));
        verify(productRepository, never()).deleteById(eq(id));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void updateById_happyCase() {
        Long id = 10L;
        Product product = new Product("Product1", "desc", new BigDecimal("100.00"), 3, "1");
        String oldName = product.getName();
        String oldDesc = product.getDescription();
        BigDecimal oldPrice = product.getPrice();
        Integer oldStock = product.getStock();
        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        ProductUpdateRequestDto productUpdateRequestDto = new ProductUpdateRequestDto();
        productUpdateRequestDto.setName("iphone 13");
        productUpdateRequestDto.setDescription("Blue one");
        productUpdateRequestDto.setPrice(new BigDecimal("999.99"));
        productUpdateRequestDto.setStock(5);

        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0, Product.class));

        ProductResponseDto productResponseDto = productServiceImpl.updateProduct(id, productUpdateRequestDto);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(captor.capture());
        Product product1 = captor.getValue();

        assertThat(product1.getName()).isEqualTo("iphone 13");
        assertThat(product1.getDescription()).isEqualTo("Blue one");
        assertThat(product1.getPrice()).isEqualByComparingTo("999.99");
        assertThat(product1.getStock()).isEqualTo(5);

        assertThat(product1.getName()).isEqualTo(productResponseDto.getName());
        assertThat(product1.getDescription()).isEqualTo(productResponseDto.getDescription());
        assertThat(product1.getPrice()).isEqualByComparingTo(productResponseDto.getPrice());
        assertThat(product1.getStock()).isEqualTo(productResponseDto.getStock());

        assertThat(product1.getName()).isNotEqualTo(oldName);
        assertThat(product1.getDescription()).isNotEqualTo(oldDesc);
        assertThat(product1.getPrice()).isNotEqualByComparingTo(oldPrice);
        assertThat(product1.getStock()).isNotEqualTo(oldStock);

        assertThat(product1).isSameAs(product);

        InOrder inOrder = Mockito.inOrder(productRepository);
        inOrder.verify(productRepository).findById(eq(id));
        inOrder.verify(productRepository).save(any(Product.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateById_no_updateTest() {
        Long id = 10L;

        when(productRepository.findById(eq(id))).thenReturn(Optional.empty());
        ProductUpdateRequestDto productUpdateRequestDto = new ProductUpdateRequestDto();

        assertThatThrownBy(() -> productServiceImpl.updateProduct(id, productUpdateRequestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("10");

        verify(productRepository, times(1)).findById(eq(id));
        verify(productRepository, never()).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void updateById_invalidNameTest() {
        Long id = 10L;
        Product product = new Product("Product1", "desc", new BigDecimal("100.00"), 3, "1");

        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        ProductUpdateRequestDto productUpdateRequestDto = new ProductUpdateRequestDto();
        productUpdateRequestDto.setName(" ");

        assertThatThrownBy(() -> productServiceImpl.updateProduct(id, productUpdateRequestDto))
                .isInstanceOf(IllegalArgumentException.class);

        verify(productRepository, times(1)).findById(eq(id));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void updateById_invalidPriceTest() {
        Long id = 10L;
        Product product = new Product("Product1", "desc", new BigDecimal("100.00"), 3, "1");

        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        ProductUpdateRequestDto productUpdateRequestDto = new ProductUpdateRequestDto();
        productUpdateRequestDto.setName("iphone 13");
        productUpdateRequestDto.setDescription("Blue one");
        productUpdateRequestDto.setStock(5);
        productUpdateRequestDto.setPrice(new BigDecimal("-100.00"));

        assertThatThrownBy(() -> productServiceImpl.updateProduct(id, productUpdateRequestDto))
                .isInstanceOf(IllegalArgumentException.class);

        verify(productRepository, times(1)).findById(eq(id));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void updateById_invalidStockTest() {
        Long id = 10L;
        Product product = new Product("Product1", "desc", new BigDecimal("100.00"), 3, "1");

        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        ProductUpdateRequestDto productUpdateRequestDto = new ProductUpdateRequestDto();
        productUpdateRequestDto.setName("iphone 13");
        productUpdateRequestDto.setPrice(new  BigDecimal("100.00"));
        productUpdateRequestDto.setDescription("Blue one");
        productUpdateRequestDto.setStock(-321321321);

        assertThatThrownBy(() -> productServiceImpl.updateProduct(id, productUpdateRequestDto))
                .isInstanceOf(IllegalArgumentException.class);

        verify(productRepository, times(1)).findById(eq(id));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void getAllTest() {
        Product product1 = new Product("Aproduct1", "descProduct1", new BigDecimal("100"), 1, "1");
        Product product2 = new Product("Bproduct2", "descProduct2", new BigDecimal("200"), 2, "2");
        Product product3 = new Product("Cproduct3", "descProduct3", new BigDecimal("300"), 3, "3");

        List<Product> productList = List.of(product1, product2, product3);
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<Product> productPage = new PageImpl<Product>(productList, pageRequest, 3);

        when(productRepository.findAll(eq(pageRequest))).thenReturn(productPage);

        Page<ProductResponseDto> obtainedAll = productServiceImpl.getAll(pageRequest);

        assertThat(obtainedAll.getContent()).isNotEmpty();
        assertThat(obtainedAll.getTotalElements()).isEqualTo(3);
        assertThat(obtainedAll.getTotalPages()).isEqualTo(1);
        assertThat(obtainedAll.getContent()).extracting(ProductResponseDto::getName)
                .containsExactly("Aproduct1", "Bproduct2", "Cproduct3");

        verify(productRepository, times(1)).findAll(eq(pageRequest));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void searchByNameTest_withoutSpaces() {
        Product product1 = new Product("Iphone 13", "descProduct1", new BigDecimal("100"), 1, "1");
        Product product2 = new Product("Iphone 14", "descProduct2", new BigDecimal("200"), 2, "2");

        List<Product> productList = List.of(product1, product2);
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"));
        Page<Product> productPage = new PageImpl<Product>(productList, pageRequest, 2);

        when(productRepository.findAllByNameContainingIgnoreCase("iphone", pageRequest)).thenReturn(productPage);

        Page<ProductResponseDto> productResponseDtos = productServiceImpl.searchByName("iphone", pageRequest);

        assertThat(productResponseDtos.getContent()).isNotEmpty();
        assertThat(productResponseDtos.getTotalElements()).isEqualTo(2);
        assertThat(productResponseDtos.getContent().size()).isEqualTo(2);

        verify(productRepository, times(1)).findAllByNameContainingIgnoreCase("iphone", pageRequest);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void searchByNameTest_withSpaces() {
        Product product1 = new Product("iphone           ", "descProduct1", new BigDecimal("100"), 1, "1");
        List<Product> productList = List.of(product1);
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"));
        Page<Product> productPage = new PageImpl<Product>(productList, pageRequest, 1);

        when(productRepository.findAllByNameContainingIgnoreCase("iphone", pageRequest)).thenReturn(productPage);

        Page<ProductResponseDto> productResponseDtos = productServiceImpl.searchByName("iphone", pageRequest);

        assertThat(productResponseDtos.getContent()).isNotEmpty();
        assertThat(productResponseDtos.getContent().get(0).getName()).isEqualTo("iphone");

        verify(productRepository, times(1)).findAllByNameContainingIgnoreCase("iphone", pageRequest);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void searchByNameTest_withIncorrectQuery() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"));

        assertThatThrownBy(() -> productServiceImpl.searchByName("a", pageRequest))
                .isInstanceOf(IllegalArgumentException.class);

        verify(productRepository, never()).findAllByNameContainingIgnoreCase(anyString(), any(PageRequest.class));
    }

    @Test
    public void searchByNameTest_withIncorrectSortName() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "incorrectName"));

        assertThatThrownBy(() -> productServiceImpl.searchByName("iphone", pageRequest))
                .isInstanceOf(IllegalArgumentException.class);

        verify(productRepository, never()).findAllByNameContainingIgnoreCase(anyString(), any(PageRequest.class));
    }

    @Test
    public void deleteById() {
        Long id = 1L;
        Product product = new Product("Product1", "desc", new BigDecimal("100.00"), 3, "1");

        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        productServiceImpl.deleteProduct(id);

        InOrder inOrder = Mockito.inOrder(productRepository);
        inOrder.verify(productRepository).findById(eq(id));
        inOrder.verify(productRepository).deleteById(eq(id));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteById_failed() {
        Long id = 10L;
        when(productRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productServiceImpl.deleteProduct(id)).isInstanceOf(NotFoundException.class);

        verify(productRepository, times(1)).deleteById(eq(id));
    }
}
