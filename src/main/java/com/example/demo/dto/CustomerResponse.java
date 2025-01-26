package com.example.demo.dto;

public record CustomerResponse(
    Long id,
    String name,
    String address,
    String email,
    String phone,
    String gstNumber,
    String vendorCode
) {}