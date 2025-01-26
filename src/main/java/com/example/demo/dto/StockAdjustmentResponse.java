package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.models.Product;

public record StockAdjustmentResponse(
    Long id,
    Product product,
    Integer quantity,
    String reason,
    LocalDate date
) {}