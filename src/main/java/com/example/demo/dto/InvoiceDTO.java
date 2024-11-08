package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceDTO {
    private LocalDate invoiceDate;
    private String invoiceNumber;
    private Long customerId; // Referencing the Customer by ID
    private Integer gstPercentage;
    private List<ProductDTO> products;

    @Data
    public static class ProductDTO {
        private Long productId; // Updated to match the product name in the entity
        private Integer qty;
        private Double price;
    }
}
