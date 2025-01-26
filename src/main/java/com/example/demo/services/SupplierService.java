package com.example.demo.services;

import com.example.demo.dto.SupplierRequest;
import com.example.demo.dto.SupplierResponse;
import java.util.List;

public interface SupplierService {
    SupplierResponse createSupplier(SupplierRequest supplierRequest);
    List<SupplierResponse> getAllSuppliers();
    SupplierResponse getSupplierById(Long id);
    void deleteSupplier(Long id);
}