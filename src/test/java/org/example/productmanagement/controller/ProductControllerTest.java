package org.example.productmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.productmanagement.dto.request.ProductRequestDto;
import org.example.productmanagement.dto.response.ProductResponseDto;
import org.example.productmanagement.exception.ProductNotFoundException;
import org.example.productmanagement.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final Long PRODUCT_ID = 1L;
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);
    private static final BigDecimal INVALID_PRICE = BigDecimal.valueOf(-10);
    private static final BigDecimal UPDATED_PRICE = BigDecimal.valueOf(200);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return product when product exists")
    void getProductById_shouldReturnProduct_whenProductExists() throws Exception {
        // given
        when(service.getById(PRODUCT_ID)).thenReturn(response());

        // when & then
        mockMvc.perform(get("/products/{id}", PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    @DisplayName("Should return 404 when product does not exist")
    void getProductById_shouldReturn404_whenProductNotFound() throws Exception {
        // given
        when(service.getById(PRODUCT_ID))
                .thenThrow(new ProductNotFoundException("Product not found"));

        // when & then
        mockMvc.perform(get("/products/{id}", PRODUCT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_shouldReturnCreated_whenValidRequest() throws Exception {
        // given
        when(service.create(request())).thenReturn(response());

        // when & then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    @DisplayName("Should return 400 when request is invalid")
    void createProduct_shouldReturn400_whenInvalidRequest() throws Exception {
        // given
        var invalidRequest =invalidRequest();

        // when & then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProduct_shouldReturnUpdatedProduct_whenValidRequest() throws Exception {
        // given
        when(service.update(PRODUCT_ID, request())).thenReturn(updatedResponse());

        // when & then
        mockMvc.perform(put("/products/{id}", PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(UPDATED_PRICE));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void deleteProduct_shouldReturnNoContent() throws Exception {
        // when & then
        mockMvc.perform(delete("/products/{id}", PRODUCT_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProducts_shouldReturnList() throws Exception {
        // given
        when(service.getAll()).thenReturn(List.of(response(), updatedResponse()));

        // when & then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should return products filtered by category")
    void getProductsByCategory_shouldReturnList() throws Exception {
        // given
        when(service.getByCategory("IT"))
                .thenReturn(List.of(response(), updatedResponse()));

        // when & then
        mockMvc.perform(get("/products/category/{category}", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private ProductRequestDto request() {
        return new ProductRequestDto(
                "Laptop",
                "desc",
                PRICE,
                "IT"
        );
    }

    private ProductRequestDto invalidRequest() {
        return new ProductRequestDto(
                "",
                null,
                INVALID_PRICE,
                ""
        );
    }

    private ProductResponseDto response() {
        return new ProductResponseDto(
                PRODUCT_ID,
                "Laptop",
                "desc",
                PRICE,
                "IT"
        );
    }

    private ProductResponseDto updatedResponse() {
        return new ProductResponseDto(
                PRODUCT_ID,
                "Phone",
                "new",
                UPDATED_PRICE,
                "ELEC"
        );
    }
}
