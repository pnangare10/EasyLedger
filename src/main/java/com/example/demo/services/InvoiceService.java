package com.example.demo.services;

import com.example.demo.dto.InvoiceDTO;
import com.example.demo.models.Invoice;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice createInvoice(InvoiceDTO invoiceDTO, UserDetails user);

    List<Invoice> getInvoices(UserDetails userDetails);

    void deleteInvoice(Long id, UserDetails createdBy);
    Optional<Invoice> getInvoiceById(Long id, UserDetails createdBy);

}
