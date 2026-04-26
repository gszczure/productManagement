package org.example.productmanagement.service;

import org.example.productmanagement.dto.request.ProductRequestDto;
import org.example.productmanagement.dto.response.ProductResponseDto;
import org.example.productmanagement.exception.ProductNotFoundException;
import org.example.productmanagement.mapper.ProductMapper;
import org.example.productmanagement.model.Product;
import org.example.productmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto create(ProductRequestDto productRequestDto) {
        Product productToSave = ProductMapper.toEntity(productRequestDto);
        return ProductMapper.toDto(productRepository.save(productToSave));
    }

    public ProductResponseDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return ProductMapper.toDto(product);
    }

    public List<ProductResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    public ProductResponseDto update(Long id, ProductRequestDto productRequestDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Product updatedProduct = existingProduct.toBuilder()
                .name(productRequestDto.name())
                .description(productRequestDto.description())
                .price(productRequestDto.price())
                .category(productRequestDto.category())
                .build();

        return ProductMapper.toDto(productRepository.save(updatedProduct));
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<ProductResponseDto> getByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }
}