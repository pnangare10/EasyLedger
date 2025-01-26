package com.example.demo.dto;

import java.time.LocalDate;

public record SalesTransactionResponse(
    Long id,
    ProductResponse product,
    Integer quantity,
    Double price,
    LocalDate date
) {}