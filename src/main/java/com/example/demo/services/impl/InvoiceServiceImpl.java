package com.example.demo.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CustomerResponse;
import com.example.demo.dto.InvoiceRequest;
import com.example.demo.dto.InvoiceResponse;
import com.example.demo.dto.ProductResponse;
import com.example.demo.dto.SalesTransactionRequest;
import com.example.demo.dto.SalesTransactionResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.models.Customer;
import com.example.demo.models.Invoice;
import com.example.demo.models.Product;
import com.example.demo.models.SalesTransaction;
import com.example.demo.models.User;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
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

        Customer customer = customerRepository.findById(invoiceRequest.customerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        invoice.setCustomer(customer);

        User createdBy = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        invoice.setCreatedBy(createdBy);

        List<SalesTransaction> salesTransactions = invoiceRequest.salesTransactions().stream()
                .map(request -> convertToEntity(request, invoice))
                .collect(Collectors.toList());
        invoice.setSalesTransactions(salesTransactions);
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

        List<SalesTransactionResponse> salesTransactionResponses = invoice.getSalesTransactions().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

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
                salesTransactionResponses,
                createdByResponse,
                invoice.getCreatedDate(),
                invoice.getUpdatedDate()
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
                salesTransaction.getProduct().getHsnCode()
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