package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // Basic CRUD operations inherited from JpaRepository
    
    // Custom queries for customer management
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByFullNameContainingIgnoreCase(String fullName);
    
    List<Customer> findByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    @Query("SELECT COUNT(c) FROM Customer c")
    long getTotalCustomers();
    
    @Query("SELECT c FROM Customer c WHERE c.createdAt >= :startDate")
    List<Customer> findCustomersRegisteredAfter(@Param("startDate") java.time.LocalDateTime startDate);
}
