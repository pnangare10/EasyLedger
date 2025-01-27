package com.example.demo.services.impl;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.models.Invoice;
import com.example.demo.models.Payment;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.services.PaymentService;
import exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public PaymentResponse addPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        
        // Validate payment amount
        if(request.amount() > invoice.getRemainingAmount()) {
            throw new IllegalArgumentException(
                "Payment exceeds outstanding amount. Remaining: " + invoice.getRemainingAmount()
            );
        }
        
        // Create payment
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setCustomer(invoice.getCustomer());
        payment.setAmount(request.amount());
        payment.setPaymentDate(request.paymentDate() != null ? 
            request.paymentDate() : LocalDate.now());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setReferenceNumber(request.referenceNumber());
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice status
        updateInvoicePaymentStatus(invoice);
        
        return convertToResponse(savedPayment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        return convertToResponseList(paymentRepository.findByInvoice(invoice));
    }

    @Override
    public List<PaymentResponse> getCustomerPayments(Long customerId) {
        return convertToResponseList(paymentRepository.findByCustomer(customerRepository.getOne(customerId)));
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getPaymentDate(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getReferenceNumber(),
            payment.getCreatedDate()
        );
    }

    private List<PaymentResponse> convertToResponseList(List<Payment> payments) {
        return payments.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private void updateInvoicePaymentStatus(Invoice invoice) {

        List<Payment> payments = paymentRepository.findByInvoice(invoice);
        double totalPaid = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        invoice.setTotalPaid(totalPaid);
        
        if(totalPaid >= invoice.getTotalAmount()) {
            invoice.setPaymentStatus("PAID");
        } else if(totalPaid > 0) {
            invoice.setPaymentStatus("PARTIALLY_PAID");
        } else {
            invoice.setPaymentStatus("UNPAID");
        }
        
        invoiceRepository.save(invoice);
    }
    
    // Add other CRUD methods
}