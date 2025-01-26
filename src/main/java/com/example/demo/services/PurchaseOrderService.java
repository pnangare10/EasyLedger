package com.example.demo.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.dto.PurchaseOrderRequest;
import com.example.demo.dto.PurchaseOrderResponse;

public interface PurchaseOrderService {
    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest purchaseOrderRequest, UserDetails userDetails);
    List<PurchaseOrderResponse> getAllPurchaseOrders();
    PurchaseOrderResponse getPurchaseOrderById(Long id);
    void deletePurchaseOrder(Long id);
}