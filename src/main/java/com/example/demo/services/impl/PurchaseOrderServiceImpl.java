package com.example.demo.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.services.UserDetailsImpl;
import com.example.demo.dto.ProductResponse;
import com.example.demo.dto.PurchaseOrderRequest;
import com.example.demo.dto.PurchaseOrderResponse;
import com.example.demo.dto.ReceivedGoodsRequest;
import com.example.demo.dto.ReceivedGoodsResponse;
import com.example.demo.dto.SupplierResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.models.Product;
import com.example.demo.models.PurchaseOrder;
import com.example.demo.models.ReceivedGoods;
import com.example.demo.models.Supplier;
import com.example.demo.models.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.PurchaseOrderService;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest purchaseOrderRequest, UserDetails userDetails) {
        PurchaseOrder purchaseOrder = convertToEntity(purchaseOrderRequest, userDetails);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return convertToResponse(purchaseOrder);
    }

    @Override
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found"));
        return convertToResponse(purchaseOrder);
    }

    @Override
    public void deletePurchaseOrder(Long id) {
        purchaseOrderRepository.deleteById(id);
    }

    private PurchaseOrder convertToEntity(PurchaseOrderRequest purchaseOrderRequest, UserDetails userDetails) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderDate(purchaseOrderRequest.orderDate());
        purchaseOrder.setOrderNumber(purchaseOrderRequest.orderNumber());
        Supplier supplier = supplierRepository.findById(purchaseOrderRequest.supplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        purchaseOrder.setSupplier(supplier);

        User createdBy = userRepository.findById(userDetailsImpl.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        purchaseOrder.setCreatedBy(createdBy);

        List<ReceivedGoods> receivedGoods = purchaseOrderRequest.receivedGoods().stream()
                .map(request -> convertToEntity(request, purchaseOrder))
                .collect(Collectors.toList());
        purchaseOrder.setReceivedGoods(receivedGoods);

        return purchaseOrder;
    }

    private ReceivedGoods convertToEntity(ReceivedGoodsRequest receivedGoodsRequest, PurchaseOrder purchaseOrder) {
        ReceivedGoods receivedGoods = new ReceivedGoods();
        Product product = productRepository.findById(receivedGoodsRequest.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        receivedGoods.setProduct(product);
        receivedGoods.setDate(receivedGoodsRequest.receivedDate());
        receivedGoods.setQuantity(receivedGoodsRequest.quantity());
        receivedGoods.setPrice(receivedGoodsRequest.price());
        receivedGoods.setPurchaseOrder(purchaseOrder);
        return receivedGoods;
    }

    private PurchaseOrderResponse convertToResponse(PurchaseOrder purchaseOrder) {
        SupplierResponse supplierResponse = new SupplierResponse(
                purchaseOrder.getSupplier().getId(),
                purchaseOrder.getSupplier().getName(),
                purchaseOrder.getSupplier().getAddress(),
                purchaseOrder.getSupplier().getContactNumber()
        );

        List<ReceivedGoodsResponse> receivedGoodsResponses = purchaseOrder.getReceivedGoods().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        UserResponse createdByResponse = new UserResponse(
                purchaseOrder.getCreatedBy().getUserId(),
                purchaseOrder.getCreatedBy().getUserName()
        );

        return new PurchaseOrderResponse(
                purchaseOrder.getId(),
                purchaseOrder.getOrderNumber(),
                purchaseOrder.getOrderDate(),
                purchaseOrder.getTotalAmount(),
                supplierResponse,
                receivedGoodsResponses,
                createdByResponse,
                purchaseOrder.getCreatedDate(),
                purchaseOrder.getUpdatedDate()
        );
    }

    private ReceivedGoodsResponse convertToResponse(ReceivedGoods receivedGoods) {
        ProductResponse productResponse = new ProductResponse(
                receivedGoods.getProduct().getId(),
                receivedGoods.getProduct().getName(),
                receivedGoods.getProduct().getDescription(),
                receivedGoods.getProduct().getSku(),
                receivedGoods.getProduct().getPrice(),
                receivedGoods.getProduct().getStockLevel(),
                receivedGoods.getProduct().getMinStockLevel(),
                receivedGoods.getProduct().getReservedStock(),
                receivedGoods.getProduct().getHsnCode(),
                receivedGoods.getProduct().getItemType(),
                receivedGoods.getProduct().getUnitOfMeasurement()
        );

        return new ReceivedGoodsResponse(
                receivedGoods.getId(),
                productResponse,
                receivedGoods.getDate(),
                receivedGoods.getQuantity(),
                receivedGoods.getPrice()
        );
    }
}