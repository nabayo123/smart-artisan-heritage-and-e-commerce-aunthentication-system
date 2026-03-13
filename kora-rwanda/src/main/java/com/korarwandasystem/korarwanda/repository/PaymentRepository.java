package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Payment;
import com.korarwandasystem.korarwanda.model.PaymentMethod;
import com.korarwandasystem.korarwanda.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Basic CRUD operations inherited from JpaRepository
    
    // Custom queries for payment management
    Optional<Payment> findByTransactionRef(String transactionRef);
    
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    
    List<Payment> findByPaymentStatus(PaymentStatus status);
    
    List<Payment> findByOrderCustomerCustomerId(Long customerId);
    
    List<Payment> findByOrderOrderId(Long orderId);
    
    boolean existsByTransactionRef(String transactionRef);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = :status")
    long countByPaymentStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = :method AND p.paymentStatus = :status")
    long countByPaymentMethodAndStatus(@Param("method") PaymentMethod method, 
                                      @Param("status") PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :dateTime")
    List<Payment> findPaymentsAfter(@Param("dateTime") LocalDateTime dateTime);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.order.totalAmount) FROM Payment p WHERE p.paymentStatus = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate")
    double getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    List<Payment> findLatestPayments();
}
