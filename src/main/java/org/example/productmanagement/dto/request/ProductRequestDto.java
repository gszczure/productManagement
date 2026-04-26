package org.example.productmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequestDto(

        @NotBlank(message = "Name cannot be empty")
        String name,

        String description,

        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be greater than 0")
        BigDecimal price,

        @NotBlank(message = "Category cannot be empty")
        String category
) {}