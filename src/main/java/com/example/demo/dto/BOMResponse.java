package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BOMResponse(
    Long id,
    String name,
    String version,
    LocalDateTime createdDate,
    ProductResponse product,
    List<BOMItemResponse> items
) {}