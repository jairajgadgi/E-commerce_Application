package com.example.ecommerce_application.service.impl;

import com.example.ecommerce_application.dto.CustomerDtos.AddressRequest;
import com.example.ecommerce_application.dto.CustomerDtos.CustomerRequest;
import com.example.ecommerce_application.dto.CustomerDtos.CustomerResponse;
import com.example.ecommerce_application.entity.Customer;
import com.example.ecommerce_application.entity.CustomerAddress;
import com.example.ecommerce_application.exception.DuplicateResourceException;
import com.example.ecommerce_application.exception.NotFoundException;
import com.example.ecommerce_application.repository.CustomerAddressRepository;
import com.example.ecommerce_application.repository.CustomerRepository;
import com.example.ecommerce_application.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerAddressRepository customerAddressRepository) {
        this.customerRepository = customerRepository;
        this.customerAddressRepository = customerAddressRepository;
    }

    @Override
    public CustomerResponse create(CustomerRequest request) {
        customerRepository.findByEmailIgnoreCase(request.email()).ifPresent(customer -> {
            throw new DuplicateResourceException("Customer already exists: " + request.email());
        });

        Customer customer = new Customer();
        customer.setFirstName(request.firstName().trim());
        customer.setLastName(request.lastName().trim());
        customer.setEmail(request.email().trim().toLowerCase());
        customer.setPhone(request.phone());
        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        return toResponse(findCustomer(id));
    }

    @Override
    public void addAddress(Long customerId, AddressRequest request) {
        Customer customer = findCustomer(customerId);
        CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        address.setLabel(request.label());
        address.setLine1(request.line1());
        address.setLine2(request.line2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
        address.setCountry(request.country());
        address.setDefaultAddress(request.defaultAddress() == null || request.defaultAddress());
        customerAddressRepository.save(address);
    }

    Customer findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getPhone());
    }
}
