package com.korarwanda.kora.dto;

import com.korarwanda.kora.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    @Data
    public static class Request {
        @NotBlank
        private String name;
        private String category;
        private String description;
        @NotNull @Positive
        private BigDecimal price;
    }

    @Data
    public static class Response {
        private Long productId;
        private String name;
        private String category;
        private String description;
        private BigDecimal price;
        private ProductStatus status;
        private String imageUrl;
        private Long artisanId;
        private String artisanName;
        private String artisanDistrict;
        private String heritageHash;
        private LocalDateTime createdAt;
    }
}
