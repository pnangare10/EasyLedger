package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record ManufacturingProcessResponse(
    Long id,
    String manufacturingId,
    Integer quantityToProduce,
    LocalDate expectedCompletionDate,
    String status,
    BOMResponse bom,
    List<ManufacturingStageResponse> stages
) {}