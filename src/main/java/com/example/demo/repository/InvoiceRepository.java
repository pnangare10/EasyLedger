package com.example.demo.repository;

import com.example.demo.models.Invoice;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByIdAndCreatedByOrderByCreatedDateDesc(Long id, User createdBy);
    List<Invoice> findAllByCreatedByOrderByCreatedDateDesc(User createdBy);
    void deleteByIdAndCreatedBy(Long id, User createdBy);

    Optional<Invoice> findByIdAndCreatedBy(Long id, User user);
}
