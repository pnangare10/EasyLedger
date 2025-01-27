package com.example.demo.dto;

public record SalesOrderItemResponse(
        Long id,
        ProductResponse product,
        Integer quantity,
        Double unitPrice
) {}