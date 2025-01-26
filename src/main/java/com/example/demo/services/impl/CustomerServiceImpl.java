package com.example.demo.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.config.services.UserDetailsImpl;
import com.example.demo.dto.CustomerRequest;
import com.example.demo.dto.CustomerResponse;
import com.example.demo.models.Customer;
import com.example.demo.models.User;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Override
    public CustomerResponse createCustomer(CustomerRequest customerRequest, UserDetails userDetails) {
        UserDetailsImpl userImpl = (UserDetailsImpl) userDetails;
        User user = userRepository.findById(userImpl.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Customer customer = new Customer();
        customer.setCustomerName(customerRequest.name());
        customer.setAddress(customerRequest.address());
        customer.setPhone(customerRequest.contactNumber());
        customer.setEmail(customerRequest.email());
        customer.setGstNumber(customerRequest.gstNumber());
        customer.setCreatedBy(user);
        customer.setVendorCode(customerRequest.vendorCode());
        customer = customerRepository.save(customer);
        return convertToResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return convertToResponse(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        customer.setCustomerName(customerRequest.name());
        customer.setAddress(customerRequest.address());
        customer.setPhone(customerRequest.contactNumber());

        customer = customerRepository.save(customer);
        return convertToResponse(customer);
    }

    private CustomerResponse convertToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getGstNumber(),
                customer.getVendorCode()
                
        );
    }
}