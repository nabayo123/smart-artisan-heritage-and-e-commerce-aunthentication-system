package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.dto.AuthDto;
import com.korarwanda.kora.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login for all user types")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login for Artisan, Customer, or Admin")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register/artisan")
    @Operation(summary = "Register a new Artisan")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> registerArtisan(
            @Valid @RequestBody AuthDto.ArtisanRegisterRequest request) {
        AuthDto.LoginResponse response = authService.registerArtisan(request);
        return ResponseEntity.ok(ApiResponse.success("Artisan registered successfully", response));
    }

    @PostMapping("/register/customer")
    @Operation(summary = "Register a new Customer")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> registerCustomer(
            @Valid @RequestBody AuthDto.CustomerRegisterRequest request) {
        AuthDto.LoginResponse response = authService.registerCustomer(request);
        return ResponseEntity.ok(ApiResponse.success("Customer registered successfully", response));
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Register a new Administrator (Public for Development)")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> registerAdmin(
            @Valid @RequestBody AuthDto.AdminRegisterRequest request) {
        AuthDto.LoginResponse response = authService.registerAdmin(request);
        return ResponseEntity.ok(ApiResponse.success("Admin registered successfully", response));
    }

    @PostMapping("/update-password")
    @Operation(summary = "Update user password for security settings")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody AuthDto.PasswordUpdateRequest request) {
        authService.updatePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }

    @PostMapping("/verify-account")
    @Operation(summary = "Verify account using email token")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(
            @Valid @RequestBody AuthDto.VerifyAccountRequest request) {
        authService.verifyAccount(request);
        return ResponseEntity.ok(ApiResponse.success("Account verified successfully", null));
    }
    @PostMapping("/resend-code")
    @Operation(summary = "Resend verification code to email")
    public ResponseEntity<ApiResponse<Void>> resendCode(
            @Valid @RequestBody AuthDto.ResendVerificationRequest request) {
        authService.resendVerificationCode(request);
        return ResponseEntity.ok(ApiResponse.success("Verification code resent successfully", null));
    }
}
