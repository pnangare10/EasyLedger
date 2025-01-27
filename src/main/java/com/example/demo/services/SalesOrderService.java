package com.example.demo.services;

import com.example.demo.dto.SalesOrderRequest;
import com.example.demo.dto.SalesOrderResponse;
import java.util.List;

public interface SalesOrderService {
    SalesOrderResponse createSalesOrder(SalesOrderRequest request);
    List<SalesOrderResponse> getAllSalesOrders();
    SalesOrderResponse getSalesOrderById(Long id);
    SalesOrderResponse updateSalesOrder(Long id, SalesOrderRequest request);
    void deleteSalesOrder(Long id);
    SalesOrderResponse confirmSalesOrder(Long id);
    SalesOrderResponse cancelSalesOrder(Long id);
}