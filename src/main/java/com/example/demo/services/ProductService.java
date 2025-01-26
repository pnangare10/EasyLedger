package com.example.demo.services;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    List<ProductResponse> getAllProducts();
    Optional<ProductResponse> getProductById(Long id);
    void deleteProduct(Long productId);
}