package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderResponse(
    Long id,
    String orderNumber,
    LocalDateTime orderDate,
    String status,
    CustomerResponse customer,
    List<SalesOrderItemResponse> items,
    ManufacturingProcessResponse manufacturingProcess,
    LocalDateTime createdDate
) {}