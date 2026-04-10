package com.korarwanda.kora.dto;

import com.korarwanda.kora.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ---- Login Request ----
public class AuthDto {

    @Data
    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String role; // ARTISAN | CUSTOMER | ADMIN
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String email;
        private String fullName;
        private UserRole role;
        private Long userId;

        public LoginResponse(String token, String email, String fullName, UserRole role, Long userId) {
            this.token = token;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
            this.userId = userId;
        }
    }

    // ---- Artisan Register Request ----
    @Data
    public static class ArtisanRegisterRequest {
        @NotBlank @Size(max = 150)
        private String fullName;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        private String phoneNumber;
        private String districtVillage;
        private String bio;
        private String momoNumber;
        private Long cooperativeId;
    }

    // ---- Customer Register Request ----
    @Data
    public static class CustomerRegisterRequest {
        @NotBlank @Size(max = 150)
        private String fullName;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        private String phone;
    }

    // ---- Admin Register Request ----
    @Data
    public static class AdminRegisterRequest {
        @NotBlank @Size(max = 150)
        private String fullName;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
    }

    // ---- Password Update ----
    @Data
    public static class PasswordUpdateRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String currentPassword;
        @NotBlank @Size(min = 6)
        private String newPassword;
        @NotBlank
        private String role; // ARTISAN | CUSTOMER | ADMIN
    }

    // ---- Account Verification ----
    @Data
    public static class VerifyAccountRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String code;
        @NotBlank
        private String role; // ARTISAN | CUSTOMER
    }

    // ---- Resend Verification ----
    @Data
    public static class ResendVerificationRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String role; // ARTISAN | CUSTOMER
    }
}
