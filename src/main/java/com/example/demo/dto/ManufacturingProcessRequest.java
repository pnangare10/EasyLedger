package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record ManufacturingProcessRequest(
    String manufacturingId,
    Long bomId,
    Integer quantityToProduce,
    LocalDate expectedCompletionDate,
    List<ManufacturingStageRequest> stages,
    Boolean reserveMaterials
) {}