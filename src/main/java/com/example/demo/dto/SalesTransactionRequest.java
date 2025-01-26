package com.example.demo.dto;

public record SalesTransactionRequest(
    Long productId,
    int quantity,
    double price
) {}