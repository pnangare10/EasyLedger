package com.example.demo.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.models.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.services.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setSku(productRequest.sku());
        product.setPrice(productRequest.price());
        product.setStockLevel(productRequest.stockLevel());
        product.setMinStockLevel(productRequest.minStockLevel());
        product.setHsnCode(productRequest.hsnCode());
        product = productRepository.save(product);
        logger.info("Product created: {}", product);
        return convertToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        logger.debug("Fetching all products");
        return productRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> getProductById(Long id) {
        logger.debug("Fetching product by id: {}", id);
        return productRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public void deleteProduct(Long productId) {
        logger.debug("Deleting product by id: {}", productId);
        try {
            productRepository.deleteById(productId);
            logger.info("Product deleted: {}", productId);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error deleting product: {}", productId, e);
            throw e;
        }
    }

    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                product.getStockLevel(),
                product.getMinStockLevel(),
                product.getHsnCode()
        );
    }
}