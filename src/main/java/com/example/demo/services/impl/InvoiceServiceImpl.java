package com.example.demo.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.dto.*;
import com.example.demo.models.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.services.InvoiceService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest, UserDetails userDetails) {
        Invoice invoice = convertToEntity(invoiceRequest, userDetails);
        invoice = invoiceRepository.save(invoice);
        return convertToResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return convertToResponse(invoice);
    }

    @Override
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    private Invoice convertToEntity(InvoiceRequest invoiceRequest, UserDetails userDetails) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(invoiceRequest.invoiceDate());
        invoice.setInvoiceNumber(invoiceRequest.invoiceNumber());
        invoice.setGstPercentage(invoiceRequest.gstPercentage());
        SalesOrder salesOrder = salesOrderRepository.findById(invoiceRequest.salesOrderId())
                .orElseThrow(() -> new RuntimeException("Sales Order not found"));

        Customer customer = customerRepository.findById(salesOrder.getCustomer().getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        invoice.setCustomer(customer);

        User createdBy = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        invoice.setCreatedBy(createdBy);

        invoice.setSalesOrder(salesOrder);
        return invoice;
    }

    private SalesTransaction convertToEntity(SalesTransactionRequest salesTransactionRequest, Invoice invoice) {
        SalesTransaction salesTransaction = new SalesTransaction();

        Product product = productRepository.findById(salesTransactionRequest.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        salesTransaction.setProduct(product);
        salesTransaction.setQuantity(salesTransactionRequest.quantity());
        salesTransaction.setPrice(salesTransactionRequest.price());
        salesTransaction.setInvoice(invoice);
        salesTransaction.setDate(invoice.getInvoiceDate());

        return salesTransaction;
    }

    private InvoiceResponse convertToResponse(Invoice invoice) {
        CustomerResponse customerResponse = new CustomerResponse(
                invoice.getCustomer().getId(),
                invoice.getCustomer().getCustomerName(),
                invoice.getCustomer().getAddress(),
                invoice.getCustomer().getEmail(),
                invoice.getCustomer().getPhone(),
                invoice.getCustomer().getGstNumber(),
                invoice.getCustomer().getVendorCode()
        );
        SalesOrder salesOrder = invoice.getSalesOrder();
        SalesOrderResponse salesOrderResponse = new SalesOrderResponse(
                salesOrder.getId(),
                salesOrder.getOrderNumber(),
                salesOrder.getOrderDate(),
                salesOrder.getStatus(),
                convertToCustomerResponse(salesOrder.getCustomer()),
                convertToSalesOrderItemsResponse(salesOrder.getItems()),
                salesOrder.getManufacturingProcess() != null ? convertToManufacturingProcess(salesOrder.getManufacturingProcess()): null,
                salesOrder.getCreatedDate()
        );

        UserResponse createdByResponse = new UserResponse(
                invoice.getCreatedBy().getUserId(),
                invoice.getCreatedBy().getUserName()
        );

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceDate(),
                invoice.getInvoiceNumber(),
                invoice.getGstPercentage(),
                invoice.getTotalAmount(),
                customerResponse,
                salesOrderResponse,
                createdByResponse,
                invoice.getCreatedDate(),
                invoice.getUpdatedDate()
        );
    }

    private ManufacturingProcessResponse convertToManufacturingProcess(ManufacturingProcess manufacturingProcess) {
        return new ManufacturingProcessResponse(
                manufacturingProcess.getId(),
                manufacturingProcess.getManufacturingId(),
                manufacturingProcess.getQuantityToProduce(),
                manufacturingProcess.getExpectedCompletionDate(),
                manufacturingProcess.getStatus(),
                convertToBOMResponse(manufacturingProcess.getBom()),
                convertToManufacturingStagesResponse(manufacturingProcess.getStages())
        );
    }

    private BOMResponse convertToBOMResponse(BillOfMaterials bom) {
        return new BOMResponse(
                bom.getId(),
                bom.getName(),
                bom.getVersion(),
                bom.getCreatedDate(),
                convertToProductResponse(bom.getProduct()),
                convertToBOMItemResponse(bom.getItems())
        );
    }

    private List<BOMItemResponse> convertToBOMItemResponse(List<BOMItem> bomItems) {
        return bomItems.stream()
                .map(bomItem -> new BOMItemResponse(
                        bomItem.getRawMaterial().getId(),
                        bomItem.getRawMaterial().getName(),
                        bomItem.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    private List<ManufacturingStageResponse> convertToManufacturingStagesResponse(List<ManufacturingStage> manufacturingStages) {
        return manufacturingStages.stream()
                .map(manufacturingStage -> new ManufacturingStageResponse(
                        manufacturingStage.getId(),
                        manufacturingStage.getName(),
                        manufacturingStage.getDueDate(),
                        manufacturingStage.getStatus(),
                        manufacturingStage.getDescription()
                ))
                .collect(Collectors.toList());
    }
    private CustomerResponse convertToCustomerResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getGstNumber(),
                customer.getVendorCode()
        );
    }

    private List<SalesOrderItemResponse> convertToSalesOrderItemsResponse(List<SalesOrderItem> salesOrderItems) {
        return salesOrderItems.stream()
                .map(salesOrderItem -> new SalesOrderItemResponse(
                        salesOrderItem.getId(),
                        convertToProductResponse(salesOrderItem.getProduct()),
                        salesOrderItem.getQuantity(),
                        salesOrderItem.getUnitPrice()

                ))
                .collect(Collectors.toList());
    }
    private ProductResponse convertToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                product.getStockLevel(),
                product.getMinStockLevel(),
                product.getReservedStock(),
                product.getHsnCode(),
                product.getItemType(),
                product.getUnitOfMeasurement()
        );
    }


    private SalesTransactionResponse convertToResponse(SalesTransaction salesTransaction) {
        ProductResponse productResponse = new ProductResponse(
                salesTransaction.getProduct().getId(),
                salesTransaction.getProduct().getName(),
                salesTransaction.getProduct().getDescription(),
                salesTransaction.getProduct().getSku(),
                salesTransaction.getProduct().getPrice(),
                salesTransaction.getProduct().getStockLevel(),
                salesTransaction.getProduct().getMinStockLevel(),
                salesTransaction.getProduct().getReservedStock(),
                salesTransaction.getProduct().getHsnCode(),
                salesTransaction.getProduct().getItemType(),
                salesTransaction.getProduct().getUnitOfMeasurement()
        );

        return new SalesTransactionResponse(
                salesTransaction.getId(),
                productResponse,
                salesTransaction.getQuantity(),
                salesTransaction.getPrice(),
                salesTransaction.getDate()
        );
    }
}