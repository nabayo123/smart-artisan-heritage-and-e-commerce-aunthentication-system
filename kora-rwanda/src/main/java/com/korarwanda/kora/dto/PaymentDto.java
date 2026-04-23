package com.korarwanda.kora.dto;

import com.korarwanda.kora.enums.PaymentMethod;
import com.korarwanda.kora.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    @Data
    public static class Request {
        @NotNull
        private Long orderId;
        @NotNull
        private PaymentMethod paymentMethod;
        private String momoNumber;
    }

    @Data
    public static class Response {
        private Long paymentId;
        private Long orderId;
        private PaymentMethod paymentMethod;
        private PaymentStatus paymentStatus;
        private String transactionRef;
        private BigDecimal amount;
        private String currency;
        private LocalDateTime createdAt;
    }
}
