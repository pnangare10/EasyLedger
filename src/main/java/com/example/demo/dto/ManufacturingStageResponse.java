package com.example.demo.dto;

import java.time.LocalDate;

public record ManufacturingStageResponse(
    Long id,
    String name,
    LocalDate dueDate,
    String status,
    String description
) {}