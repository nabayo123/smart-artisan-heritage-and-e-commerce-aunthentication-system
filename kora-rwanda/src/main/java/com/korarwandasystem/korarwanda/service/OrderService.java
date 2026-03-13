package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.*;
import com.korarwandasystem.korarwanda.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getCustomer() == null || order.getCustomer().getCustomerId() == null) {
            throw new IllegalArgumentException("Order must be associated with a customer");
        }
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order total amount must be greater than zero");
        }

        Optional<Customer> customer = customerRepository.findById(order.getCustomer().getCustomerId());
        if (customer.isEmpty()) {
            throw new IllegalArgumentException("Associated customer not found");
        }

        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCustomer(customer.get());

        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order orderDetails) {
        if (orderDetails == null) {
            throw new IllegalArgumentException("Order details cannot be null");
        }

        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + id);
        }

        Order order = optionalOrder.get();

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot modify a delivered order");
        }

        if (orderDetails.getTotalAmount() != null && orderDetails.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
            order.setTotalAmount(orderDetails.getTotalAmount());
        }

        if (orderDetails.getOrderStatus() != null) {
            if (!isValidStatusTransition(order.getOrderStatus(), orderDetails.getOrderStatus())) {
                throw new IllegalStateException("Invalid status transition from " +
                        order.getOrderStatus() + " to " + orderDetails.getOrderStatus());
            }
            order.setOrderStatus(orderDetails.getOrderStatus());
        }

        if (orderDetails.getCustomer() != null && orderDetails.getCustomer().getCustomerId() != null) {
            customerRepository.findById(orderDetails.getCustomer().getCustomerId())
                    .ifPresent(order::setCustomer);
        }

        if (orderDetails.getOrderDate() != null) {
            order.setOrderDate(orderDetails.getOrderDate());
        }

        return orderRepository.save(order);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.PAID || newStatus == OrderStatus.PENDING;
            case PAID -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.PAID;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.SHIPPED;
            case DELIVERED -> newStatus == OrderStatus.DELIVERED;
            default -> false;
        };
    }

    public boolean deleteOrder(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return false;
        }

        Order order = optionalOrder.get();

        if (order.getOrderStatus() == OrderStatus.PAID ||
                order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot delete an order that is " + order.getOrderStatus());
        }

        List<Payment> payments = paymentRepository.findByOrderOrderId(id);
        paymentRepository.deleteAll(payments);

        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(id);
        orderItemRepository.deleteAll(orderItems);

        orderRepository.deleteById(id);
        return true;
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerCustomerId(customerId);
    }
    
    public List<Order> getOrdersByCustomerIdAndStatus(Long customerId, OrderStatus status) {
        return orderRepository.findByCustomerCustomerIdAndOrderStatus(customerId, status);
    }

    public List<Order> getOrdersByArtisanId(Long artisanId) {
        return orderRepository.findByArtisanId(artisanId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByOrderStatus(OrderStatus.PENDING);
    }

    public List<Order> getCompletedOrders() {
        return orderRepository.findByOrderStatus(OrderStatus.DELIVERED);
    }

    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    public long getTotalOrdersCount() {
        return orderRepository.count();
    }

    public long getOrdersByStatusCount(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setOrderStatus(status);
            return orderRepository.save(order);
        }
        throw new IllegalArgumentException("Order not found with id: " + orderId);
    }

    public OrderItem addOrderItem(Long orderId, OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }
        
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            orderItem.setOrder(order);

            if (orderItem.getProduct() != null && orderItem.getProduct().getProductId() != null) {
                productRepository.findById(orderItem.getProduct().getProductId())
                        .ifPresent(orderItem::setProduct);
            }

            return orderItemRepository.save(orderItem);
        }
        throw new IllegalArgumentException("Order not found with id: " + orderId);
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderOrderId(orderId);
    }

    public boolean deleteOrderItem(Long orderItemId) {
        if (orderItemRepository.existsById(orderItemId)) {
            orderItemRepository.deleteById(orderItemId);
            return true;
        }
        return false;
    }

    public Order calculateOrderTotal(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);

            BigDecimal total = items.stream()
                    .map(item -> {
                        BigDecimal price = (item.getProduct() != null) ? item.getProduct().getPrice() : item.getPrice();
                        return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setTotalAmount(total);
            return orderRepository.save(order);
        }
        throw new IllegalArgumentException("Order not found with id: " + orderId);
    }
}