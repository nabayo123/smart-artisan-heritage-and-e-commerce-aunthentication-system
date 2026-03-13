package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.*;
import com.korarwandasystem.korarwanda.repository.*;
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
    private OrderRepository orderRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {
        // Validation
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        if (payment.getOrder() == null || payment.getOrder().getOrderId() == null) {
            throw new IllegalArgumentException("Payment must be associated with an order");
        }
        if (payment.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if (payment.getTransactionRef() == null || payment.getTransactionRef().trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction reference is required");
        }
        
        // Check if payment already exists for this order
        Optional<Payment> existingPayment = paymentRepository.findByOrderOrderId(payment.getOrder().getOrderId())
                .stream()
                .findFirst();
        if (existingPayment.isPresent()) {
            throw new IllegalStateException("Payment already exists for this order");
        }
        
        // Validate order exists
        Optional<Order> order = orderRepository.findById(payment.getOrder().getOrderId());
        if (!order.isPresent()) {
            throw new IllegalArgumentException("Associated order not found");
        }
        
        // Set initial status and timestamps
        payment.setPaymentStatus(PaymentStatus.INITIATED);
        payment.setOrder(order.get());
        
        return paymentRepository.save(payment);
    }

    public Payment updatePayment(Long id, Payment paymentDetails) {
        // Validation
        if (paymentDetails == null) {
            throw new IllegalArgumentException("Payment details cannot be null");
        }
        
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (!optionalPayment.isPresent()) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }

        Payment payment = optionalPayment.get();
        
        // Business validation - prevent modification of completed payments
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot modify a completed payment");
        }
        
        // Update allowed fields
        if (paymentDetails.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        }
        
        if (paymentDetails.getTransactionRef() != null && !paymentDetails.getTransactionRef().trim().isEmpty()) {
            // Check for duplicate transaction reference
            if (paymentRepository.existsByTransactionRef(paymentDetails.getTransactionRef()) &&
                !payment.getTransactionRef().equals(paymentDetails.getTransactionRef())) {
                throw new IllegalArgumentException("Transaction reference already exists");
            }
            payment.setTransactionRef(paymentDetails.getTransactionRef());
        }
        
        if (paymentDetails.getPaymentStatus() != null) {
            payment.setPaymentStatus(paymentDetails.getPaymentStatus());
        }

        // Update order association if provided
        if (paymentDetails.getOrder() != null && paymentDetails.getOrder().getOrderId() != null) {
            Optional<Order> order = orderRepository.findById(paymentDetails.getOrder().getOrderId());
            if (order.isPresent()) {
                payment.setOrder(order.get());
            } else {
                throw new IllegalArgumentException("Associated order not found");
            }
        }

        return paymentRepository.save(payment);
    }

    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        // Validation
        if (paymentId == null) {
            throw new IllegalArgumentException("Payment ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }
        
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (!optionalPayment.isPresent()) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentId);
        }
        
        Payment payment = optionalPayment.get();
        
        // Business validation - prevent reverting completed payments
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS && status != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot change status from SUCCESS to " + status);
        }
        
        payment.setPaymentStatus(status);
        
        // Business logic for successful payments
        if (status == PaymentStatus.SUCCESS) {
            if (payment.getOrder() == null) {
                throw new IllegalStateException("Payment is not associated with any order");
            }
            
            Optional<Order> optionalOrder = orderRepository.findById(payment.getOrder().getOrderId());
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                order.setOrderStatus(OrderStatus.PAID);
                orderRepository.save(order);
            } else {
                throw new IllegalStateException("Associated order not found");
            }
        }
        
        return paymentRepository.save(payment);
    }

    public boolean deletePayment(Long id) {
        // Validation
        if (id == null) {
            throw new IllegalArgumentException("Payment ID cannot be null");
        }
        
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (!optionalPayment.isPresent()) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }
        
        Payment payment = optionalPayment.get();
        
        // Business validation - prevent deletion of completed payments
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot delete a completed payment");
        }
        
        // Check if payment is associated with an order and update order status if needed
        if (payment.getOrder() != null) {
            Optional<Order> optionalOrder = orderRepository.findById(payment.getOrder().getOrderId());
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                // Reset order status if payment is being deleted
                if (order.getOrderStatus() == OrderStatus.PAID) {
                    order.setOrderStatus(OrderStatus.PENDING);
                    orderRepository.save(order);
                }
            }
        }
        
        paymentRepository.deleteById(id);
        return true;
    }

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderOrderId(orderId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    public List<Payment> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method);
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

    public List<Payment> getPaymentsByDateRange(LocalDateTime startDate,
                                                LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    public List<Payment> getPaymentsByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionRef(transactionId)
                .map(List::of)
                .orElse(List.of());
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository
                .findByTransactionRef(transactionId)
                .stream()
                .findFirst();
    }

    public long getTotalPaymentsCount() {
        return paymentRepository.count();
    }

    public long getPaymentsByStatusCount(PaymentStatus status) {
        return paymentRepository.countByPaymentStatus(status);
    }

    public double getTotalRevenueByDateRange(LocalDateTime startDate,
                                             LocalDateTime endDate) {
        return paymentRepository.getTotalRevenueByDateRange(startDate, endDate);
    }

    public Payment processMobileMoneyPayment(Long paymentId,
                                             String transactionId) {

        Optional<Payment> optionalPayment =
                paymentRepository.findById(paymentId);

        if (optionalPayment.isPresent()) {

            Payment payment = optionalPayment.get();

            payment.setTransactionRef(transactionId);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);

            paymentRepository.save(payment);

            return updatePaymentStatus(paymentId, PaymentStatus.SUCCESS);
        }

        return null;
    }

    public Payment processPayment(Long paymentId, String transactionId) {

        Optional<Payment> optionalPayment =
                paymentRepository.findById(paymentId);

        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setTransactionRef(transactionId);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            
            return updatePaymentStatus(paymentId, PaymentStatus.SUCCESS);
        }
        return null;
    }

    public Payment refundPayment(Long paymentId) {

        Optional<Payment> optionalPayment =
                paymentRepository.findById(paymentId);

        if (optionalPayment.isPresent()) {

            Payment payment = optionalPayment.get();

            payment.setPaymentStatus(PaymentStatus.FAILED);

            return paymentRepository.save(payment);
        }

        return null;
    }
}