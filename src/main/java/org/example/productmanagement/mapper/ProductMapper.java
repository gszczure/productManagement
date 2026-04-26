package org.example.productmanagement.mapper;

import org.example.productmanagement.dto.request.ProductRequestDto;
import org.example.productmanagement.dto.response.ProductResponseDto;
import org.example.productmanagement.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public static Product toEntity(ProductRequestDto productRequestDto) {
        return Product.builder()
                .name(productRequestDto.name())
                .description(productRequestDto.description())
                .price(productRequestDto.price())
                .category(productRequestDto.category())
                .build();
    }

    public static ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory()
        );
    }
}