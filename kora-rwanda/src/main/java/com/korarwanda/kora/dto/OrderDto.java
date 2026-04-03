package com.korarwanda.kora.dto;

import com.korarwanda.kora.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Data
    public static class CreateRequest {
        @NotEmpty
        private List<OrderItemRequest> items;
    }

    @Data
    public static class OrderItemRequest {
        @NotNull
        private Long productId;
        @Positive
        private Integer quantity;
    }

    @Data
    public static class Response {
        private Long orderId;
        private Long customerId;
        private String customerName;
        private LocalDateTime orderDate;
        private BigDecimal totalAmount;
        private OrderStatus orderStatus;
        private List<OrderItemResponse> orderItems;
    }

    @Data
    public static class OrderItemResponse {
        private Long orderItemId;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String heritageHash;
    }
}
