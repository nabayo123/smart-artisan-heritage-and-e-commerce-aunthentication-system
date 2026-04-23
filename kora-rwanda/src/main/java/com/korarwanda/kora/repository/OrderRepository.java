package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Order;
import com.korarwanda.kora.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer_CustomerId(Long customerId);
    List<Order> findByBuyerId(Long buyerId);
    List<Order> findByOrderStatus(OrderStatus status);
    
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"customer"})
    List<Order> findAll();
    
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM PlatformOrder o")
    java.math.BigDecimal getTotalRevenue();
}
