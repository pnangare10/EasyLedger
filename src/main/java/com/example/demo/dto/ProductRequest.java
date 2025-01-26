package com.example.demo.dto;

public record ProductRequest(
    String name,
    String description,
    String sku,
    double price,
    int stockLevel,
    int minStockLevel,
    String hsnCode
) {}