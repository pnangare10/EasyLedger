package com.example.demo.dto;

import java.time.LocalDate;

public record ReceivedGoodsResponse(
    Long id,
    ProductResponse product,
    LocalDate receivedDate,
    Integer quantity,
    Double price
) {}