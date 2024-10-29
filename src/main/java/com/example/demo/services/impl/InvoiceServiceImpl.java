package com.example.demo.services.impl;

import com.example.demo.dto.InvoiceDTO;
import com.example.demo.models.Invoice;
import com.example.demo.models.User;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Invoice createInvoice(InvoiceDTO invoiceDTO, UserDetails userDetails) {
        User user = userRepository.findByUserName(userDetails.getUsername()).get();
        System.out.println(user);
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
        invoice.setCustomerName(invoiceDTO.getCustomerName());
        invoice.setGstPercentage(invoiceDTO.getGstPercentage());
        invoice.setCreatedBy(user);
        double totalAmount = invoiceDTO.getProducts().stream()
                .mapToDouble(p -> p.getPrice() * p.getQty())
                .sum();
        totalAmount += totalAmount * invoiceDTO.getGstPercentage()/100;
        invoice.setTotalAmount(totalAmount);
        List<Invoice.Product> products = invoiceDTO.getProducts().stream().map(p -> {
            Invoice.Product product = new Invoice.Product();
            product.setProduct(p.getProduct());
            product.setQty(p.getQty());
            product.setPrice(p.getPrice());
            return product;
        }).collect(Collectors.toList());

        invoice.setProducts(products);
        return invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getInvoices(String username) {
        List<Invoice> invoices = invoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
        return invoices;
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id, UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUserName(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            invoiceRepository.deleteByIdAndCreatedBy(id, user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Optional<Invoice> getInvoiceById(Long id, UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUserName(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return invoiceRepository.findByIdAndCreatedBy(id, user);
        } else {
            throw new RuntimeException("User not found");
        }

    }
}
