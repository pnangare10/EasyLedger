package com.example.demo.dto;

import java.util.List;

public record BOMRequest(
    String name,
    Long productId,
    String version,
    List<BOMItemRequest> items
) {}