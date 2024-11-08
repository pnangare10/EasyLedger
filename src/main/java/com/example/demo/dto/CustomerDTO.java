package com.example.demo.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String customerName;
    private String address;
    private String email;
    private String phone;
    private String gstNumber;
    private String companyName;
}
