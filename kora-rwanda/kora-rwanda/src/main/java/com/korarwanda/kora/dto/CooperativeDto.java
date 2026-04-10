package com.korarwanda.kora.dto;

import com.korarwanda.kora.enums.VerificationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class CooperativeDto {

    @Data
    public static class Request {
        @NotBlank
        private String name;
        private String province;
        private String district;
        private String contactPhone;
    }

    @Data
    public static class Response {
        private Long cooperativeId;
        private String name;
        private String province;
        private String district;
        private String contactPhone;
        private VerificationStatus verificationStatus;
        private LocalDateTime createdAt;
        private int artisanCount;
    }
}
