package com.korarwanda.kora.dto;

import com.korarwanda.kora.enums.CertificateStatus;
import lombok.Data;

import java.time.LocalDate;

public class CertificateDto {

    @Data
    public static class Response {
        private Long certificateId;
        private String heritageHash;
        private String qrCodeData;
        private String qrCodeImageUrl;
        private LocalDate issueDate;
        private CertificateStatus verificationStatus;
        private Long productId;
        private String productName;
        private String artisanName;
        private String artisanBio;
        private String artisanDistrict;
        private String cooperativeName;
    }

    @Data
    public static class VerifyResponse {
        private boolean authentic;
        private String message;
        private Response certificate;
    }
}
