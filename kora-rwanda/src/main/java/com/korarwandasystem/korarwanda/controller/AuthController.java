package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.dto.AuthResponse;
import com.korarwandasystem.korarwanda.dto.LoginRequest;
import com.korarwandasystem.korarwanda.dto.RegisterRequest;
import com.korarwandasystem.korarwanda.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestParam String token) {
        String newToken = authService.refreshToken(token);
        return ResponseEntity.ok(newToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            return ResponseEntity.ok(authService.getCurrentUser());
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
    }

    @GetMapping("/check-admin")
    public ResponseEntity<Boolean> checkIfAdmin() {
        try {
            boolean isAdmin = authService.isCurrentUserAdmin();
            return ResponseEntity.ok(isAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(false);
        }
    }

    @GetMapping("/check-artisan")
    public ResponseEntity<Boolean> checkIfArtisan() {
        try {
            boolean isArtisan = authService.isCurrentUserArtisan();
            return ResponseEntity.ok(isArtisan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(false);
        }
    }

    @GetMapping("/check-customer")
    public ResponseEntity<Boolean> checkIfCustomer() {
        try {
            boolean isCustomer = authService.isCurrentUserCustomer();
            return ResponseEntity.ok(isCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(false);
        }
    }
}
