package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

// PaymentResponse.java
public record PaymentResponse(
    Long id,
    LocalDate paymentDate,
    Double amount,
    String paymentMethod,
    String referenceNumber,
    LocalDateTime createdDate
) {}