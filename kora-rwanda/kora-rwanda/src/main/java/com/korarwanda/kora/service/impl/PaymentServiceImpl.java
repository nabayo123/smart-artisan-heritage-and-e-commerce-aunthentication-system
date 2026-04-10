package com.korarwanda.kora.service.impl;

import com.korarwanda.kora.dto.PaymentDto;
import com.korarwanda.kora.entity.Order;
import com.korarwanda.kora.entity.Payment;
import com.korarwanda.kora.enums.OrderStatus;
import com.korarwanda.kora.enums.PaymentStatus;
import com.korarwanda.kora.exception.BadRequestException;
import com.korarwanda.kora.exception.ResourceNotFoundException;
import com.korarwanda.kora.repository.OrderRepository;
import com.korarwanda.kora.repository.PaymentRepository;
import com.korarwanda.kora.service.PaymentService;
import com.korarwanda.kora.util.HeritageTagUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final HeritageTagUtil heritageTagUtil;

    @Override
    @Transactional
    public PaymentDto.Response initiatePayment(PaymentDto.Request request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId()));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is not in PENDING status. Current: " + order.getOrderStatus());
        }

        if (paymentRepository.findByOrder_OrderId(order.getOrderId()).isPresent()) {
            throw new BadRequestException("A payment already exists for order: " + order.getOrderId());
        }

        String transactionRef = heritageTagUtil.generateTransactionRef(request.getPaymentMethod().name());

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.INITIATED)
                .transactionRef(transactionRef)
                .amount(order.getTotalAmount())
                .currency("RWF")
                .build();

        return toResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentDto.Response confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        // Simulate gateway confirmation (in production, this would verify with MTN/Airtel API)
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.getOrder().setOrderStatus(OrderStatus.PAID);
        orderRepository.save(payment.getOrder());

        return toResponse(paymentRepository.save(payment));
    }

    @Override
    public PaymentDto.Response getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order", orderId));
        return toResponse(payment);
    }

    @Override
    public List<PaymentDto.Response> getAll() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private PaymentDto.Response toResponse(Payment p) {
        PaymentDto.Response r = new PaymentDto.Response();
        r.setPaymentId(p.getPaymentId());
        r.setPaymentMethod(p.getPaymentMethod());
        r.setPaymentStatus(p.getPaymentStatus());
        r.setTransactionRef(p.getTransactionRef());
        r.setAmount(p.getAmount());
        r.setCurrency(p.getCurrency());
        r.setCreatedAt(p.getCreatedAt());
        if (p.getOrder() != null) {
            r.setOrderId(p.getOrder().getOrderId());
        }
        return r;
    }
}
