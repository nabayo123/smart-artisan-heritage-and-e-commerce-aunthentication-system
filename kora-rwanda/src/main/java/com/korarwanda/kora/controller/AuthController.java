package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.dto.AuthDto;
import com.korarwanda.kora.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login for all user types")
public class AuthController {

    @Autowired
    private AuthService authService;

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
}
