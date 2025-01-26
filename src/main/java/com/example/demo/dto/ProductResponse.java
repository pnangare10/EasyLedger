package com.example.demo.dto;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String sku,
        Double price,
        Integer stockLevel,
        Integer minStockLevel,
        String hsnCode
) {}