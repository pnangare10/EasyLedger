package com.example.demo.dto;

public record BOMItemRequest(
    Long rawMaterialId,
    Integer quantity
) {}