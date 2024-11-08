package com.example.demo.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String productName;
    private Double productPrice;
    private Integer gstPercentage;
}
