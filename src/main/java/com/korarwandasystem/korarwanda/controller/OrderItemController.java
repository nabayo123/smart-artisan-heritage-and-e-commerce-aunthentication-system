package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.OrderItem;
import com.korarwandasystem.korarwanda.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@CrossOrigin(origins = "*")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(orderItems);
    }

    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@Valid @RequestBody OrderItem orderItem) {
        OrderItem createdOrderItem = orderItemService.createOrderItem(orderItem);
        return ResponseEntity.status(201).body(createdOrderItem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        OrderItem orderItem = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(orderItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @Valid @RequestBody OrderItem orderItemDetails) {
        OrderItem updatedOrderItem = orderItemService.updateOrderItem(id, orderItemDetails);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrder(@PathVariable Long orderId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByProduct(@PathVariable Long productId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByProductId(productId);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/by-order-and-product")
    public ResponseEntity<OrderItem> getOrderItemByOrderAndProduct(@RequestParam Long orderId, 
                                                                 @RequestParam Long productId) {
        OrderItem orderItem = orderItemService.getOrderItemByOrderAndProduct(orderId, productId);
        return ResponseEntity.ok(orderItem);
    }

    @GetMapping("/quantity-greater-than")
    public ResponseEntity<List<OrderItem>> getOrderItemsByQuantityGreaterThan(@RequestParam int minQuantity) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByQuantityGreaterThan(minQuantity);
        return ResponseEntity.ok(orderItems);
    }
}
