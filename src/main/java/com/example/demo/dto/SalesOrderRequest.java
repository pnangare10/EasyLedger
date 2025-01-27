package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderRequest(
    Long customerId,
    List<SalesOrderItemRequest> items,
    LocalDateTime orderDate,
    String status,
    Long manufacturingProcessId
) {}