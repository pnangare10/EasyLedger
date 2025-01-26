package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponse(
    Long id,
    LocalDate invoiceDate,
    String invoiceNumber,
    Integer gstPercentage,
    Double totalAmount,
    CustomerResponse customer,
    List<SalesTransactionResponse> salesTransactions,
    UserResponse createdBy,
    LocalDateTime createdDate,
    LocalDateTime updatedDate
) {}