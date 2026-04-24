package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findFirstByEmailOrderByCreatedAtDesc(String email);
    
    default Optional<Customer> findByEmail(String email) {
        return findFirstByEmailOrderByCreatedAtDesc(email);
    }
    
    boolean existsByEmail(String email);
}
