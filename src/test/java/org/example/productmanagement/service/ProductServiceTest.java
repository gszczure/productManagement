package org.example.productmanagement.service;

import org.example.productmanagement.dto.request.ProductRequestDto;
import org.example.productmanagement.exception.ProductNotFoundException;
import org.example.productmanagement.model.Product;
import org.example.productmanagement.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long SECOND_PRODUCT_ID = 2L;
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);
    private static final BigDecimal UPDATED_PRICE = BigDecimal.valueOf(200);

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    @DisplayName("Should return product when product exists")
    void getById_shouldReturnProduct_whenProductExists() {
        // given
        when(repository.findById(PRODUCT_ID)).thenReturn(Optional.of(product()));

        // when
        var result = service.getById(PRODUCT_ID);

        // then
        assertThat(result.name()).isEqualTo("Laptop");
        verify(repository).findById(PRODUCT_ID);
    }

    @Test
    @DisplayName("Should throw exception when product does not exist")
    void getById_shouldThrowException_whenProductNotFound() {
        // given
        when(repository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.getById(PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found");

        verify(repository).findById(PRODUCT_ID);
    }

    @Test
    @DisplayName("Should create product successfully")
    void create_shouldSaveProduct_whenValidRequest() {
        // given
        when(repository.save(any())).thenReturn(product());

        // when
        var result = service.create(request());

        // then
        assertThat(result.id()).isEqualTo(PRODUCT_ID);
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should update product when product exists")
    void update_shouldUpdateProduct_whenProductExists() {
        // given
        when(repository.findById(PRODUCT_ID)).thenReturn(Optional.of(product()));
        when(repository.save(any())).thenReturn(product().toBuilder()
                .name("Phone")
                .price(UPDATED_PRICE)
                .category("ELEC")
                .build());

        // when
        var result = service.update(PRODUCT_ID, updatedRequest());

        // then
        assertThat(result.name()).isEqualTo("Phone");
        assertThat(result.price()).isEqualTo(UPDATED_PRICE);
        verify(repository).findById(PRODUCT_ID);
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating non existing product")
    void update_shouldThrowException_whenProductNotFound() {
        // given
        when(repository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.update(PRODUCT_ID, request()))
                .isInstanceOf(ProductNotFoundException.class);

        verify(repository).findById(PRODUCT_ID);
    }

    @Test
    @DisplayName("Should delete product by id")
    void delete_shouldCallRepositoryDeleteById() {
        // when
        service.delete(PRODUCT_ID);

        // then
        verify(repository).deleteById(PRODUCT_ID);
    }

    @Test
    @DisplayName("Should return all products")
    void getAll_shouldReturnListOfProducts() {
        // given
        when(repository.findAll()).thenReturn(List.of(product(), secondProduct()));

        // when
        var result = service.getAll();

        // then
        assertThat(result)
                .hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("Laptop", "Phone");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should return products filtered by category")
    void getByCategory_shouldReturnProducts_whenCategoryExists() {
        // given
        when(repository.findByCategory("IT")).thenReturn(List.of(product(), secondProduct()));

        // when
        var result = service.getByCategory("IT");

        // then
        assertThat(result)
                .hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("Laptop", "Phone");
        verify(repository).findByCategory("IT");
    }

    private Product product() {
        return Product.builder()
                .id(PRODUCT_ID)
                .name("Laptop")
                .description("desc")
                .price(PRICE)
                .category("IT")
                .build();
    }

    private Product secondProduct() {
        return Product.builder()
                .id(SECOND_PRODUCT_ID)
                .name("Phone")
                .description("desc2")
                .price(UPDATED_PRICE)
                .category("IT")
                .build();
    }

    private ProductRequestDto request() {
        return new ProductRequestDto(
                "Laptop",
                "desc",
                PRICE,
                "IT"
        );
    }

    private ProductRequestDto updatedRequest() {
        return new ProductRequestDto(
                "Phone",
                "new",
                UPDATED_PRICE,
                "ELEC"
        );
    }
}