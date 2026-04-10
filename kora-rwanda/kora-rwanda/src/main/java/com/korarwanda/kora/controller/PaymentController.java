package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.dto.PaymentDto;
import com.korarwanda.kora.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Process MTN MoMo and Airtel Money payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ARTISAN', 'ADMIN')")
    @Operation(summary = "Initiate a payment for an order")
    public ResponseEntity<ApiResponse<PaymentDto.Response>> initiate(
            @Valid @RequestBody PaymentDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payment initiated. Awaiting confirmation.", paymentService.initiatePayment(request)));
    }

    @PostMapping("/{paymentId}/confirm")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ARTISAN', 'ADMIN')")
    @Operation(summary = "Confirm/simulate payment success (MoMo callback)")
    public ResponseEntity<ApiResponse<PaymentDto.Response>> confirm(@PathVariable Long paymentId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payment confirmed successfully. Funds transferred to artisan.",
                paymentService.confirmPayment(paymentId)));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get payment details by order ID")
    public ResponseEntity<ApiResponse<PaymentDto.Response>> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved",
                paymentService.getByOrderId(orderId)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payments (Admin only)")
    public ResponseEntity<ApiResponse<List<PaymentDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("All payments retrieved",
                paymentService.getAll()));
    }
}
