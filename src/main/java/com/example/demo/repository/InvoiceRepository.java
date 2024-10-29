package com.example.demo.repository;

import com.example.demo.models.Invoice;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByIdAndCreatedBy(Long id, User createBy);

    void deleteByIdAndCreatedBy(Long id, User createdBy);
}
