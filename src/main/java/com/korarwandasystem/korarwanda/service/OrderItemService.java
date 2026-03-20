package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.OrderItem;
import com.korarwandasystem.korarwanda.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderItem not found with id: " + id));
    }

    public OrderItem updateOrderItem(Long id, OrderItem orderItemDetails) {
        OrderItem orderItem = getOrderItemById(id);
        
        orderItem.setOrder(orderItemDetails.getOrder());
        orderItem.setProduct(orderItemDetails.getProduct());
        orderItem.setQuantity(orderItemDetails.getQuantity());
        orderItem.setUnitPrice(orderItemDetails.getUnitPrice());

        return orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(Long id) {
        OrderItem orderItem = getOrderItemById(id);
        orderItemRepository.delete(orderItem);
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderOrderId(orderId);
    }

    public List<OrderItem> getOrderItemsByProductId(Long productId) {
        return orderItemRepository.findByProductProductId(productId);
    }

    public OrderItem getOrderItemByOrderAndProduct(Long orderId, Long productId) {
        return orderItemRepository.findByOrderOrderIdAndProductProductId(orderId, productId);
    }

    public List<OrderItem> getOrderItemsByQuantityGreaterThan(int minQuantity) {
        return orderItemRepository.findByQuantityGreaterThan(minQuantity);
    }
}
