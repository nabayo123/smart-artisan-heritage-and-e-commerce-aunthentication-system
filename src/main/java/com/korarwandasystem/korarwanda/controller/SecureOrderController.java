package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.Order;
import com.korarwandasystem.korarwanda.model.OrderStatus;
import com.korarwandasystem.korarwanda.service.OrderService;
import com.korarwandasystem.korarwanda.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/secure/orders")
public class SecureOrderController {

    private final OrderService orderService;
    private final AuthService authService;

    public SecureOrderController(OrderService orderService, AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    // READ OPERATIONS
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER') or hasRole('ARTISAN')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, Authentication authentication) {
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) return ResponseEntity.notFound().build();

        Order order = orderOpt.get();
        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        boolean isArtisan = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTISAN"));

        if (isCustomer && !orderService.isCustomerOrder(id, authentication.getName()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (isArtisan && !orderService.isArtisanOrder(id, authentication.getName()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(order);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        String customerEmail = authentication.getName();
        return ResponseEntity.ok(orderService.getOrdersByCustomerEmail(customerEmail));
    }

    @GetMapping("/received-orders")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<List<Order>> getReceivedOrders(Authentication authentication) {
        String artisanEmail = authentication.getName();
        return ResponseEntity.ok(orderService.getOrdersByArtisanEmail(artisanEmail));
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order, Authentication authentication) {
        String customerEmail = authentication.getName();
        order.setCustomer(orderService.getCustomerObjectByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer profile not found")));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(order));
    }

    // UPDATE
    @PutMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id,
                                             @Valid @RequestBody Order order,
                                             Authentication authentication) {
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) return ResponseEntity.notFound().build();

        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        if (isCustomer && !orderService.isCustomerOrder(id, authentication.getName()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    @PatchMapping("/id/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
                                                   @RequestParam OrderStatus status,
                                                   Authentication authentication) {
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) return ResponseEntity.notFound().build();

        Order order = orderOpt.get();
        boolean isArtisan = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARTISAN"));

        if (isArtisan && !orderService.isArtisanOrder(id, authentication.getName()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (isArtisan && !(status == OrderStatus.PROCESSING || status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        order.setOrderStatus(status);
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    @PatchMapping("/id/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id, Authentication authentication) {
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) return ResponseEntity.notFound().build();

        Order order = orderOpt.get();
        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

        if (isCustomer && !orderService.isCustomerOrder(id, authentication.getName()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (isCustomer && order.getOrderStatus() != OrderStatus.PENDING)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        order.setOrderStatus(OrderStatus.CANCELLED);
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    // DELETE
    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (orderService.deleteOrder(id))
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    // FILTERED QUERIES
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @authService.canAccessUserResource(#customerId))")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/artisan/{artisanId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('ARTISAN') and @authService.canAccessUserResource(#artisanId))")
    public ResponseEntity<List<Order>> getOrdersByArtisan(@PathVariable Long artisanId) {
        return ResponseEntity.ok(orderService.getOrdersByArtisanId(artisanId));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersByDateRange(@RequestParam String startDate,
                                                            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            return ResponseEntity.ok(orderService.getOrdersByDateRange(start, end));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Invalid date format
        }
    }

    // ADMIN METRICS
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalCount() {
        return ResponseEntity.ok(orderService.getTotalOrdersCount());
    }

    @GetMapping("/pending/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getPendingCount() {
        return ResponseEntity.ok((long) orderService.getOrdersByStatus(OrderStatus.PENDING).size());
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(orderService.getTotalRevenue());
    }
}