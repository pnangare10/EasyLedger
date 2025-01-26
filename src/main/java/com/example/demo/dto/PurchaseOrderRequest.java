package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record PurchaseOrderRequest(
    LocalDate orderDate,
    String orderNumber,
    Long supplierId,
    List<ReceivedGoodsRequest> receivedGoods
) {}

