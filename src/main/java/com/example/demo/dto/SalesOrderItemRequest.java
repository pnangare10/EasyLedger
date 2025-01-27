package com.example.demo.dto;

public record SalesOrderItemRequest(
    Long productId,
    Integer quantity,
    Double unitPrice
) {}