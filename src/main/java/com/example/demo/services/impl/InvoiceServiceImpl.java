package com.example.demo.services.impl;

import com.example.demo.dto.InvoiceDTO;
import com.example.demo.models.Customer;
import com.example.demo.models.Invoice;
import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.ProductRepository;
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

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Invoice createInvoice(InvoiceDTO invoiceDTO, UserDetails userDetails) {
        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User cannot be null"));

        Customer customer = customerRepository.findById(invoiceDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
        invoice.setGstPercentage(invoiceDTO.getGstPercentage());
        invoice.setCreatedBy(user);
        invoice.setCustomer(customer);

        double totalAmount = invoiceDTO.getProducts().stream()
                .mapToDouble(p -> p.getPrice() * p.getQty())
                .sum();
        totalAmount += totalAmount * invoiceDTO.getGstPercentage() / 100;
        invoice.setTotalAmount(totalAmount);

        List<Invoice.Product> products = invoiceDTO.getProducts().stream().map(p -> {
            Product productEntity = productRepository.findById(p.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            Invoice.Product product = new Invoice.Product();
            product.setProduct(productEntity);
            product.setQty(p.getQty());
            product.setPrice(p.getPrice());
            return product;
        }).collect(Collectors.toList());

        invoice.setProducts(products);

        return invoiceRepository.save(invoice);
    }


    @Override
    public List<Invoice> getInvoices(UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUserName(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Invoice> invoices = invoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
            return invoices;
        } else {
            throw new RuntimeException("Invoices not found");
        }
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
