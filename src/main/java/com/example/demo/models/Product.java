package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String sku;
    private String hsnCode;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Integer reservedStock = 0;

    private Integer stockLevel;
    private Integer minStockLevel;
    private String unitOfMeasurement;
    private String itemType; // e.g., "Raw Material", "Finished Product", "Both"

    // Add to existing class
    public void reserveStock(int quantity) {
        this.reservedStock += quantity;
    }

    public void releaseReservedStock(int quantity) {
        this.reservedStock = Math.max(0, this.reservedStock - quantity);
    }
}
