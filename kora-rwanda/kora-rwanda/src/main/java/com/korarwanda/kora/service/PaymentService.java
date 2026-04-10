package com.korarwanda.kora.service;

import com.korarwanda.kora.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    PaymentDto.Response initiatePayment(PaymentDto.Request request);
    PaymentDto.Response confirmPayment(Long paymentId);
    PaymentDto.Response getByOrderId(Long orderId);
    List<PaymentDto.Response> getAll();
}
