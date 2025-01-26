package com.example.demo.dto;

public record StockAdjustmentRequest(
    Long productId,
    Integer quantity,
    String type,
    String reason,
    String date
) {}