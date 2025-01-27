package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

// PaymentRequest.java
public record PaymentRequest(
    @NotNull Long invoiceId,
    @Positive Double amount,
    LocalDate paymentDate,
    String paymentMethod,
    String referenceNumber
) {}