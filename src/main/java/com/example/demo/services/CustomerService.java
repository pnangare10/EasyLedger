package com.example.demo.services;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.models.Customer;
import com.example.demo.models.User;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;


    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO, UserDetails userDetails) {
        // Find the user who is creating the customer
        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Convert DTO to entity and set the 'createdBy' user
        Customer customer = convertToEntity(customerDTO);
        customer.setCreatedBy(user);  // Setting the createdBy field

        // Save and convert back to DTO
        customer = customerRepository.save(customer);
        return convertToDTO(customer);
    }


    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.setCustomerName(customerDTO.getCustomerName());
            customer.setAddress(customerDTO.getAddress());
            customer.setEmail(customerDTO.getEmail());
            customer.setPhone(customerDTO.getPhone());
            customer.setGstNumber(customerDTO.getGstNumber());
//            customer.setCompanyName(customerDTO.getCompanyName());
            return convertToDTO(customerRepository.save(customer));
        }
        return null;
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setAddress(customer.getAddress());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setGstNumber(customer.getGstNumber());
//        dto.setCompanyName(customer.getCompanyName());
        return dto;
    }

    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerName(dto.getCustomerName());
        customer.setAddress(dto.getAddress());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setGstNumber(dto.getGstNumber());
//        customer.setCompanyName(dto.getCompanyName());
        return customer;
    }
}
