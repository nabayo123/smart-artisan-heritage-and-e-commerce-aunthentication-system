package com.korarwanda.kora.service;

import com.korarwanda.kora.dto.OrderDto;
import com.korarwanda.kora.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderDto.Response createOrder(Long customerId, OrderDto.CreateRequest request);
    OrderDto.Response getById(Long orderId);
    List<OrderDto.Response> getByCustomer(Long customerId);
    List<OrderDto.Response> getAll();
    OrderDto.Response updateStatus(Long orderId, OrderStatus status);
}
