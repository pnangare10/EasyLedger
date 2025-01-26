package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record PurchaseOrderResponse(
    Long id,
    String orderNumber,
    LocalDate orderDate,
    Double totalAmount,
    SupplierResponse supplier,
    List<ReceivedGoodsResponse> receivedGoods,
    UserResponse createdBy,
    LocalDate createdDate,
    LocalDate updatedDate
) {}
