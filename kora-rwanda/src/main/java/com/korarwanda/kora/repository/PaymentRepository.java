package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Payment;
import com.korarwanda.kora.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder_OrderId(Long orderId);
    Optional<Payment> findByTransactionRef(String transactionRef);
    List<Payment> findByPaymentStatus(PaymentStatus status);
}
