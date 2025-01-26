package com.example.demo.services;

import com.example.demo.dto.InvoiceRequest;
import com.example.demo.dto.InvoiceResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest invoiceRequest, UserDetails userDetails);
    List<InvoiceResponse> getAllInvoices();
    InvoiceResponse getInvoiceById(Long id);
    void deleteInvoice(Long id);
}