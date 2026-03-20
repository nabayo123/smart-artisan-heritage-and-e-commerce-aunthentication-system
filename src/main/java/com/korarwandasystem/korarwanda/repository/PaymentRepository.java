package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Payment;
import com.korarwandasystem.korarwanda.model.PaymentMethod;
import com.korarwandasystem.korarwanda.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    Optional<Payment> findFirstByOrderId(Long orderId);

    List<Payment> findByPaymentStatus(PaymentStatus status);

    List<Payment> findByPaymentMethod(PaymentMethod method);

    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);

    Optional<Payment> findByTransactionRef(String transactionRef);

    List<Payment> findByTransactionRefContaining(String transactionRef);

    long countByPaymentStatus(PaymentStatus status);

    // Optional: Total revenue by date range
    // Use @Query if you want to calculate sum in DB
}