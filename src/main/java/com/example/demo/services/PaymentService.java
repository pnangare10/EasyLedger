package com.example.demo.services;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.models.Invoice;

import java.util.List;

public interface PaymentService {
    public PaymentResponse addPayment(PaymentRequest request);

    List<PaymentResponse> getPaymentsByInvoice(Long invoiceId);

    List<PaymentResponse> getCustomerPayments(Long customerId);


}