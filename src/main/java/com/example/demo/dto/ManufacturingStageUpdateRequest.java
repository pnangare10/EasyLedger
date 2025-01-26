package com.example.demo.dto;

import java.time.LocalDate;

public record ManufacturingStageUpdateRequest(
    String status,
    LocalDate dueDate,
    String description
) {}