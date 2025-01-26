package com.example.demo.dto;

public record BOMItemResponse(
    Long rawMaterialId,
    String rawMaterialName,
    Integer quantity
) {}