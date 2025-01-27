package com.example.demo.controller;

import com.example.demo.dto.SalesOrderRequest;
import com.example.demo.dto.SalesOrderResponse;
import com.example.demo.services.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<SalesOrderResponse> createSalesOrder(
            @RequestBody SalesOrderRequest request) {
        SalesOrderResponse response = salesOrderService.createSalesOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponse> getSalesOrder(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.getSalesOrderById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllSalesOrders() {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesOrderResponse> updateSalesOrder(@PathVariable Long id, @RequestBody SalesOrderRequest request) {
        return ResponseEntity.ok(salesOrderService.updateSalesOrder(id, request));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<SalesOrderResponse> confirmSalesOrder(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.confirmSalesOrder(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SalesOrderResponse> cancelSalesOrder(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.cancelSalesOrder(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSalesOrder(@PathVariable Long id) {
        salesOrderService.deleteSalesOrder(id);
        return ResponseEntity.noContent().build();
    }
}