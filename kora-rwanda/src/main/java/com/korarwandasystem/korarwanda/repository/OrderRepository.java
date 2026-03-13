package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Order;
import com.korarwandasystem.korarwanda.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerCustomerId(Long customerId);
    
    List<Order> findByOrderStatus(OrderStatus status);
    
    List<Order> findByCustomerCustomerIdAndOrderStatus(Long customerId, OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    long countByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customer.email = :email")
    List<Order> findByCustomerEmail(@Param("email") String email);
    
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi JOIN oi.product p WHERE p.artisan.artisanId = :artisanId")
    List<Order> findByArtisanId(@Param("artisanId") Long artisanId);
    
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi JOIN oi.product p WHERE p.artisan.artisanId = :artisanId AND o.orderStatus = :status")
    List<Order> findByArtisanIdAndStatus(@Param("artisanId") Long artisanId, @Param("status") OrderStatus status);
    
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi JOIN oi.product p WHERE p.artisan.email = :email")
    List<Order> findByArtisanEmail(@Param("email") String email);
}
