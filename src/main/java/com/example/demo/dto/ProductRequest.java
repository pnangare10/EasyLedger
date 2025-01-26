package com.example.demo.dto;

public record ProductRequest(
    String name,
    String description,
    String sku,
    Double price,
    Integer stockLevel,
    Integer minStockLevel,
    Integer reservedStock,
    String hsnCode,
    String itemType,
    String unitOfMeasurement
) {}
