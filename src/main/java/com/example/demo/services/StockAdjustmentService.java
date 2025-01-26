package com.example.demo.services;

import java.util.List;

import com.example.demo.dto.StockAdjustmentRequest;
import com.example.demo.dto.StockAdjustmentResponse;

public interface StockAdjustmentService {
    StockAdjustmentResponse createStockAdjustment(StockAdjustmentRequest stockAdjustmentRequest);
    List<StockAdjustmentResponse> getAllStockAdjustments();
    StockAdjustmentResponse getStockAdjustmentById(Long id);
    void deleteStockAdjustment(Long id);
}