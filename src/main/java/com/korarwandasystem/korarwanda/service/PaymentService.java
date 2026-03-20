package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.*;
import com.korarwandasystem.korarwanda.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService; // Use OrderService for order updates

    // --- CRUD ---

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {
        if (payment == null || payment.getOrder() == null || payment.getOrder().getId() == null) {
            throw new IllegalArgumentException("Payment must be associated with an order");
        }

        payment.setPaymentStatus(PaymentStatus.INITIATED);
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Payment updatePayment(Long id, Payment details) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (details.getPaymentMethod() != null) payment.setPaymentMethod(details.getPaymentMethod());
        if (details.getTransactionRef() != null) payment.setTransactionRef(details.getTransactionRef());
        if (details.getPaymentStatus() != null) payment.setPaymentStatus(details.getPaymentStatus());
        if (details.getAmount() != null) payment.setAmount(details.getAmount());

        return paymentRepository.save(payment);
    }

    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setPaymentStatus(status);

        // Update order status if payment is successful
        if (status == PaymentStatus.SUCCESS && payment.getOrder() != null) {
            orderService.updateOrderStatus(payment.getOrder().getId(), OrderStatus.PAID);
        }

        return paymentRepository.save(payment);
    }

    public boolean deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Optional: revert order status if payment was SUCCESS
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS && payment.getOrder() != null) {
            orderService.updateOrderStatus(payment.getOrder().getId(), OrderStatus.PENDING);
        }

        paymentRepository.deleteById(id);
        return true;
    }

    // --- Queries ---

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    public List<Payment> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method);
    }

    public List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentDateBetween(start, end);
    }

    public Optional<Payment> getPaymentByTransactionRef(String transactionRef) {
        return paymentRepository.findByTransactionRef(transactionRef);
    }

    public List<Payment> getCompletedPayments() {
        return paymentRepository.findByPaymentStatus(PaymentStatus.SUCCESS);
    }

    public List<Payment> getPendingPayments() {
        return paymentRepository.findByPaymentStatus(PaymentStatus.INITIATED);
    }

    public List<Payment> getFailedPayments() {
        return paymentRepository.findByPaymentStatus(PaymentStatus.FAILED);
    }

    public Payment processMobileMoneyPayment(Long paymentId, String transactionRef) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setPaymentMethod(PaymentMethod.MOBILE_MONEY);
        payment.setTransactionRef(transactionRef);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());

        // Mark order as PAID using OrderService
        if (payment.getOrder() != null) {
            orderService.updateOrderStatus(payment.getOrder().getId(), OrderStatus.PAID);
        }

        return paymentRepository.save(payment);
    }

    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        // Revert order to PENDING if it was PAID
        if (payment.getOrder() != null) {
            orderService.updateOrderStatus(payment.getOrder().getId(), OrderStatus.PENDING);
        }

        return paymentRepository.save(payment);
    }

    public double getTotalRevenueByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentDateBetween(start, end)
                .stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(p -> p.getAmount().doubleValue())
                .sum();
    }

    public List<Payment> getPaymentsByTransactionIdList(String transactionId) {
        return paymentRepository.findByTransactionRefContaining(transactionId);
    }

    public long getTotalPaymentsCount() {
        return paymentRepository.count();
    }

    public long getPaymentsByStatusCount(PaymentStatus status) {
        return paymentRepository.countByPaymentStatus(status);
    }
}