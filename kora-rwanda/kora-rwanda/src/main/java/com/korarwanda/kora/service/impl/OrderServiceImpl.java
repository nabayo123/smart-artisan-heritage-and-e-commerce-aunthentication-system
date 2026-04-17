package com.korarwanda.kora.service.impl;

import com.korarwanda.kora.dto.OrderDto;
import com.korarwanda.kora.entity.*;
import com.korarwanda.kora.enums.OrderStatus;
import com.korarwanda.kora.enums.ProductStatus;
import com.korarwanda.kora.exception.BadRequestException;
import com.korarwanda.kora.exception.ResourceNotFoundException;
import com.korarwanda.kora.repository.*;
import com.korarwanda.kora.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CertificateRepository certificateRepository;
    private final ArtisanRepository artisanRepository;

    @Override
    @Transactional
    public OrderDto.Response createOrder(Long userId, OrderDto.CreateRequest request) {
        // Search for the user in both repositories for testing flexibility
        Customer customer = customerRepository.findById(userId).orElse(null);
        Artisan artisan = null;
        
        if (customer == null) {
            artisan = artisanRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User (Customer or Artisan)", userId));
        }

        Order order = new Order();
        order.setBuyerId(userId); // Track who bought it regardless of entity type
        if (customer != null) {
            order.setCustomer(customer);
        } else if (artisan != null) {
            log.info("🛒 Artisan Buying Item: {} by {}", artisan.getFullName(), artisan.getEmail());
        }
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderDto.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemReq.getProductId()));

            if (product.getStatus() != ProductStatus.AVAILABLE) {
                throw new BadRequestException("Product '" + product.getName() + "' is no longer available");
            }

            Certificate cert = certificateRepository.findByProduct_ProductId(product.getProductId()).orElse(null);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .heritageHash(cert != null ? cert.getHeritageHash() : null)
                    .build();

            items.add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }

        order.setTotalAmount(total);
        order.setOrderItems(items);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    public OrderDto.Response getById(Long orderId) {
        return toResponse(orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId)));
    }

    @Override
    public List<OrderDto.Response> getByCustomer(Long customerId) {
        // Find orders where either the linked customer ID matches OR the buyerId matches
        List<Order> orders = orderRepository.findByBuyerId(customerId);
        if (orders.isEmpty()) {
            orders = orderRepository.findByCustomer_CustomerId(customerId);
        }
        return orders.stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<OrderDto.Response> getAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public OrderDto.Response updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        order.setOrderStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private OrderDto.Response toResponse(Order o) {
        OrderDto.Response r = new OrderDto.Response();
        r.setOrderId(o.getOrderId());
        r.setOrderDate(o.getOrderDate());
        r.setTotalAmount(o.getTotalAmount());
        r.setOrderStatus(o.getOrderStatus());
        if (o.getCustomer() != null) {
            r.setCustomerId(o.getCustomer().getCustomerId());
            r.setCustomerName(o.getCustomer().getFullName());
        }
        if (o.getOrderItems() != null) {
            r.setOrderItems(o.getOrderItems().stream().map(item -> {
                OrderDto.OrderItemResponse ir = new OrderDto.OrderItemResponse();
                ir.setOrderItemId(item.getOrderItemId());
                ir.setQuantity(item.getQuantity());
                ir.setUnitPrice(item.getUnitPrice());
                ir.setHeritageHash(item.getHeritageHash());
                if (item.getProduct() != null) {
                    ir.setProductId(item.getProduct().getProductId());
                    ir.setProductName(item.getProduct().getName());
                }
                return ir;
            }).collect(Collectors.toList()));
        }
        return r;
    }
}
