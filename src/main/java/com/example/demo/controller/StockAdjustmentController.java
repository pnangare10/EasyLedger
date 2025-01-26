package com.example.demo.controller;

import com.example.demo.dto.StockAdjustmentRequest;
import com.example.demo.dto.StockAdjustmentResponse;
import com.example.demo.services.StockAdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-adjustments")
@RequiredArgsConstructor
public class StockAdjustmentController {

    private final StockAdjustmentService stockAdjustmentService;

    @PostMapping
    public ResponseEntity<StockAdjustmentResponse> createStockAdjustment(@RequestBody StockAdjustmentRequest stockAdjustmentRequest) {
        StockAdjustmentResponse createdStockAdjustment = stockAdjustmentService.createStockAdjustment(stockAdjustmentRequest);
        return ResponseEntity.ok(createdStockAdjustment);
    }

    @GetMapping
    public ResponseEntity<List<StockAdjustmentResponse>> getAllStockAdjustments() {
        List<StockAdjustmentResponse> stockAdjustments = stockAdjustmentService.getAllStockAdjustments();
        return ResponseEntity.ok(stockAdjustments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockAdjustmentResponse> getStockAdjustmentById(@PathVariable Long id) {
        StockAdjustmentResponse stockAdjustment = stockAdjustmentService.getStockAdjustmentById(id);
        return stockAdjustment != null ? ResponseEntity.ok(stockAdjustment) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockAdjustment(@PathVariable Long id) {
        stockAdjustmentService.deleteStockAdjustment(id);
        return ResponseEntity.noContent().build();
    }
}