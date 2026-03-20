package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderOrderId(Long orderId);
    
    List<OrderItem> findByProductProductId(Long productId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId AND oi.product.productId = :productId")
    OrderItem findByOrderOrderIdAndProductProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.productId = :productId")
    Long getTotalQuantityByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    Long countItemsByOrderId(@Param("orderId") Long orderId);
    
    List<OrderItem> findByQuantityGreaterThan(Integer minQuantity);
}
