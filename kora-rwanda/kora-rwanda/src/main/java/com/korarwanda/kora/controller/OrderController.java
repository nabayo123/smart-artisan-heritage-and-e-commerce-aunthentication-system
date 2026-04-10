package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.dto.OrderDto;
import com.korarwanda.kora.enums.OrderStatus;
import com.korarwanda.kora.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Place and manage orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ARTISAN', 'ADMIN')")
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<OrderDto.Response>> createOrder(
            @PathVariable Long customerId,
            @Valid @RequestBody OrderDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully",
                orderService.createOrder(customerId, request)));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderDto.Response>> getById(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Order retrieved",
                orderService.getById(orderId)));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Get all orders for a customer")
    public ResponseEntity<ApiResponse<List<OrderDto.Response>>> getByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success("Customer orders retrieved",
                orderService.getByCustomer(customerId)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders (Admin only)")
    public ResponseEntity<ApiResponse<List<OrderDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("All orders retrieved",
                orderService.getAll()));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status (Admin only)")
    public ResponseEntity<ApiResponse<OrderDto.Response>> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated",
                orderService.updateStatus(orderId, status)));
    }
}
