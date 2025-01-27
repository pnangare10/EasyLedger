package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(
    LocalDate invoiceDate,
    String invoiceNumber,
    int gstPercentage,
    Long salesOrderId
) {}