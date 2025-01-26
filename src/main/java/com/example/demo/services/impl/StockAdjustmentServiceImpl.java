package com.example.demo.services.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.StockAdjustmentRequest;
import com.example.demo.dto.StockAdjustmentResponse;
import com.example.demo.models.Product;
import com.example.demo.models.StockAdjustment;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockAdjustmentRepository;
import com.example.demo.services.StockAdjustmentService;

@Service
public class StockAdjustmentServiceImpl implements StockAdjustmentService {

    @Autowired
    private StockAdjustmentRepository stockAdjustmentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public StockAdjustmentResponse createStockAdjustment(StockAdjustmentRequest stockAdjustmentRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Product product = productRepository.findById(stockAdjustmentRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setProduct(product);
        stockAdjustment.setQuantity(stockAdjustmentRequest.quantity());
        stockAdjustment.setReason(stockAdjustmentRequest.reason());
        stockAdjustment.setDate(LocalDate.parse(stockAdjustmentRequest.date(), formatter));
        switch (stockAdjustmentRequest.type()) {
            case "IN":
                product.setStockLevel(product.getStockLevel() + stockAdjustmentRequest.quantity());
                break;
            case "OUT":
                product.setStockLevel(product.getStockLevel() - stockAdjustmentRequest.quantity());
                break;
            default:
                throw new IllegalArgumentException("Invalid type");
        }
        productRepository.save(product);
        stockAdjustment = stockAdjustmentRepository.save(stockAdjustment);
        return convertToResponse(stockAdjustment);
    }

    @Override
    public List<StockAdjustmentResponse> getAllStockAdjustments() {
        return stockAdjustmentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StockAdjustmentResponse getStockAdjustmentById(Long id) {
        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock Adjustment not found"));
        return convertToResponse(stockAdjustment);
    }

    @Override
    public void deleteStockAdjustment(Long id) {
        stockAdjustmentRepository.deleteById(id);
    }

    private StockAdjustmentResponse convertToResponse(StockAdjustment stockAdjustment) {
        return new StockAdjustmentResponse(
                stockAdjustment.getId(),
                stockAdjustment.getProduct(),
                stockAdjustment.getQuantity(),
                stockAdjustment.getReason(),
                stockAdjustment.getDate()
        );
    }
}