package com.example.demo.repository;

import com.example.demo.models.Customer;
import com.example.demo.models.Invoice;
import com.example.demo.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoice(Invoice invoice);
    List<Payment> findByCustomer(Customer one);
}