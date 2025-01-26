package com.example.demo.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.dto.CustomerRequest;
import com.example.demo.dto.CustomerResponse;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest customerRequest, UserDetails userDetails);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(Long id);
    CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest);
    void deleteCustomer(Long id);
}