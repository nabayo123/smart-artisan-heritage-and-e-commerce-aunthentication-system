package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.Customer;
import com.korarwandasystem.korarwanda.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Constructor injection - the modern Spring way
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer createCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (customer.getEmail() != null && existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + customer.getEmail() + " already exists");
        }
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        if (customerDetails == null) {
            throw new IllegalArgumentException("Customer details cannot be null");
        }

        return customerRepository.findById(id).map(customer -> {
            if (customerDetails.getFirstName() != null) {
                customer.setFirstName(customerDetails.getFirstName());
            }
            if (customerDetails.getLastName() != null) {
                customer.setLastName(customerDetails.getLastName());
            }
            if (customerDetails.getEmail() != null) {
                customer.setEmail(customerDetails.getEmail());
            }
            if (customerDetails.getPhone() != null) {
                customer.setPhone(customerDetails.getPhone());
            }
            if (customerDetails.getAddress() != null) {
                customer.setAddress(customerDetails.getAddress());
            }
            if (customerDetails.getCity() != null) {
                customer.setCity(customerDetails.getCity());
            }
            if (customerDetails.getProvince() != null) {
                customer.setProvince(customerDetails.getProvince());
            }
            if (customerDetails.getDistrict() != null) {
                customer.setDistrict(customerDetails.getDistrict());
            }
            return customerRepository.save(customer);
        }).orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
    }

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Customer> getCustomersByCity(String city) {
        return customerRepository.findByCity(city);
    }

    public List<Customer> getCustomersByProvince(String province) {
        return customerRepository.findByProvince(province);
    }

    public List<Customer> getCustomersByDistrict(String district) {
        return customerRepository.findByDistrict(district);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public long getTotalCustomersCount() {
        return customerRepository.count();
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }
}