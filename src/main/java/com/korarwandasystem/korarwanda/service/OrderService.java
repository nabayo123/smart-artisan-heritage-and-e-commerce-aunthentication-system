package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.Order;
import com.korarwandasystem.korarwanda.model.Customer;
import com.korarwandasystem.korarwanda.model.Artisan;
import com.korarwandasystem.korarwanda.model.OrderStatus;
import com.korarwandasystem.korarwanda.repository.OrderRepository;
import com.korarwandasystem.korarwanda.repository.CustomerRepository;
import com.korarwandasystem.korarwanda.repository.ArtisanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ArtisanRepository artisanRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ArtisanRepository artisanRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.artisanRepository = artisanRepository;
    }

    public List<Order> getAllOrders() { return orderRepository.findAll(); }

    public Optional<Order> getOrderById(Long id) { return orderRepository.findById(id); }

    public Order createOrder(Order order) { return orderRepository.save(order); }

    public Order updateOrder(Long id, Order order) {
        order.setId(id);
        return orderRepository.save(order);
    }

    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean isCustomerOrder(Long orderId, String customerEmail) {
        return orderRepository.findById(orderId)
                .map(o -> o.getCustomer().getEmail().equals(customerEmail))
                .orElse(false);
    }

    public boolean isArtisanOrder(Long orderId, String artisanEmail) {
        return orderRepository.findById(orderId)
                .map(o -> o.getArtisan().getEmail().equals(artisanEmail))
                .orElse(false);
    }

    public Optional<Customer> getCustomerObjectByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<Artisan> getArtisanObjectByEmail(String email) {
        return artisanRepository.findByEmail(email);
    }

    public List<Order> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }

    public List<Order> getOrdersByArtisanEmail(String email) {
        return orderRepository.findByArtisanEmail(email);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByArtisanId(Long artisanId) {
        return orderRepository.findByArtisanId(artisanId);
    }

    public List<Order> getOrdersByDateRange(LocalDate start, LocalDate end) {
        return orderRepository.findByOrderDateBetween(start, end);
    }

    public Long getTotalOrdersCount() { return orderRepository.count(); }

    public Double getTotalRevenue() {
        return orderRepository.findAll().stream().mapToDouble(Order::getTotalPrice).sum();
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }
}