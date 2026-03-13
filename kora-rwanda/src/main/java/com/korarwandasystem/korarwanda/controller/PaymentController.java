package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.Payment;
import com.korarwandasystem.korarwanda.model.PaymentMethod;
import com.korarwandasystem.korarwanda.model.PaymentStatus;
import com.korarwandasystem.korarwanda.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        Payment createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @Valid @RequestBody Payment payment) {
        Payment updatedPayment = paymentService.updatePayment(id, payment);
        if (updatedPayment != null) {
            return ResponseEntity.ok(updatedPayment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        boolean deleted = paymentService.deletePayment(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/method/{method}")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable PaymentMethod method) {
        List<Payment> payments = paymentService.getPaymentsByMethod(method);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Payment>> getCompletedPayments() {
        List<Payment> payments = paymentService.getCompletedPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Payment>> getPendingPayments() {
        List<Payment> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/failed")
    public ResponseEntity<List<Payment>> getFailedPayments() {
        List<Payment> payments = paymentService.getFailedPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<Payment>> getPaymentsByTransactionId(@PathVariable String transactionId) {
        List<Payment> payments = paymentService.getPaymentsByTransactionId(transactionId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/transaction/{transactionId}/single")
    public ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId) {
        return paymentService.getPaymentByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalPaymentsCount() {
        long count = paymentService.getTotalPaymentsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> getPaymentsByStatusCount(@PathVariable PaymentStatus status) {
        long count = paymentService.getPaymentsByStatusCount(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        double revenue = paymentService.getTotalRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Long id, @RequestParam PaymentStatus status) {
        Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
        if (updatedPayment != null) {
            return ResponseEntity.ok(updatedPayment);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/process-mobile-money")
    public ResponseEntity<Payment> processMobileMoneyPayment(
            @PathVariable Long id, 
            @RequestParam String transactionId) {
        Payment processedPayment = paymentService.processMobileMoneyPayment(id, transactionId);
        if (processedPayment != null) {
            return ResponseEntity.ok(processedPayment);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        Payment refundedPayment = paymentService.refundPayment(id);
        if (refundedPayment != null) {
            return ResponseEntity.ok(refundedPayment);
        }
        return ResponseEntity.notFound().build();
    }
}
