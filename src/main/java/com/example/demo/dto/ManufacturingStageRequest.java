package com.example.demo.dto;

import java.time.LocalDate;

public record ManufacturingStageRequest(
    String name,
    LocalDate dueDate,
    String status,
    String description
) {}