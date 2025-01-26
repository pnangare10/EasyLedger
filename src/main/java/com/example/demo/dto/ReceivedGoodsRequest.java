package com.example.demo.dto;

import java.time.LocalDate;

public record ReceivedGoodsRequest(
    Long productId,
    LocalDate receivedDate,
    Integer quantity,
    Double price
) {}