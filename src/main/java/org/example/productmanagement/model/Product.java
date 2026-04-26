package org.example.productmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //TODO: przy postgresSQL zrobic UUID zamiast Longa

    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Category cannot be empty")
    private String category;
}
