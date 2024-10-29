package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceDTO {
    private LocalDate invoiceDate;
    private String invoiceNumber;
    private String customerName;
    private Integer gstPercentage;
    private List<ProductDTO> products;

    @Data
    public static class ProductDTO {
        private String product;
        private Integer qty;
        private Double price;
    }
}
