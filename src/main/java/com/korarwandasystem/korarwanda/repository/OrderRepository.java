package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Order;
import com.korarwandasystem.korarwanda.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.customer.email = :email")
    List<Order> findByCustomerEmail(@Param("email") String email);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi JOIN oi.product p WHERE p.artisan.email = :email")
    List<Order> findByArtisanEmail(@Param("email") String email);

    List<Order> findByOrderStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT o FROM Order o WHERE o.artisan.id = :artisanId")
    List<Order> findByArtisanId(@Param("artisanId") Long artisanId);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
}