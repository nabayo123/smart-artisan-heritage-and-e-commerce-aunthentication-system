package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.Order;
import com.korarwandasystem.korarwanda.model.OrderItem;
import com.korarwandasystem.korarwanda.model.OrderStatus;
import com.korarwandasystem.korarwanda.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody Order order) {
        Order updatedOrder = orderService.updateOrder(id, order);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        boolean deleted = orderService.deleteOrder(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/artisan/{artisanId}")
    public ResponseEntity<List<Order>> getOrdersByArtisanId(@PathVariable Long artisanId) {
        List<Order> orders = orderService.getOrdersByArtisanId(artisanId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Order>> getPendingOrders() {
        List<Order> orders = orderService.getPendingOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Order>> getCompletedOrders() {
        List<Order> orders = orderService.getCompletedOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalOrdersCount() {
        long count = orderService.getTotalOrdersCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> getOrdersByStatusCount(@PathVariable OrderStatus status) {
        long count = orderService.getOrdersByStatusCount(status);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> addOrderItem(@PathVariable Long orderId, @Valid @RequestBody OrderItem orderItem) {
        OrderItem createdOrderItem = orderService.addOrderItem(orderId, orderItem);
        if (createdOrderItem != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderItem);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItem> items = orderService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/items/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        boolean deleted = orderService.deleteOrderItem(orderItemId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/calculate-total")
    public ResponseEntity<Order> calculateOrderTotal(@PathVariable Long id) {
        Order updatedOrder = orderService.calculateOrderTotal(id);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.notFound().build();
    }
}
