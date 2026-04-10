package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.entity.Admin;
import com.korarwanda.kora.entity.Artisan;
import com.korarwanda.kora.enums.VerificationStatus;
import com.korarwanda.kora.exception.BadRequestException;
import com.korarwanda.kora.exception.ResourceNotFoundException;
import com.korarwanda.kora.repository.AdminRepository;
import com.korarwanda.kora.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Platform management, oversight and analytics")
public class AdminController {

    private final ArtisanRepository artisanRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;
    private final CooperativeRepository cooperativeRepository;
    private final PasswordEncoder passwordEncoder;

    // ---- Dashboard Summary ----

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get platform statistics for the admin dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalArtisans", artisanRepository.count());
        stats.put("pendingArtisans", artisanRepository.countByVerificationStatus(VerificationStatus.PENDING));
        stats.put("approvedArtisans", artisanRepository.countByVerificationStatus(VerificationStatus.APPROVED));
        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalProducts", productRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalCooperatives", cooperativeRepository.count());
        
        java.math.BigDecimal revenue = orderRepository.getTotalRevenue();
        stats.put("totalRevenue", revenue != null ? revenue : java.math.BigDecimal.ZERO);
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved", stats));
    }

    // ---- Artisan Management ----

    @GetMapping("/artisans")
    @Operation(summary = "Get all artisans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllArtisans() {
        List<Map<String, Object>> result = artisanRepository.findAll().stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("artisanId", a.getArtisanId());
            m.put("fullName", a.getFullName());
            m.put("email", a.getEmail());
            m.put("phoneNumber", a.getPhoneNumber());
            m.put("districtVillage", a.getDistrictVillage());
            m.put("verificationStatus", a.getVerificationStatus());
            m.put("cooperative", a.getCooperative() != null ? a.getCooperative().getName() : null);
            m.put("createdAt", a.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All artisans retrieved", result));
    }

    @GetMapping("/artisans/pending")
    @Operation(summary = "Get all pending artisan registrations")
    public ResponseEntity<ApiResponse<List<Artisan>>> getPendingArtisans() {
        return ResponseEntity.ok(ApiResponse.success("Pending artisans retrieved",
                artisanRepository.findByVerificationStatus(VerificationStatus.PENDING)));
    }

    @PatchMapping("/artisans/{artisanId}/verify")
    @Operation(summary = "Approve or reject an artisan registration")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyArtisan(
            @PathVariable Long artisanId,
            @RequestParam VerificationStatus status) {
        Artisan artisan = artisanRepository.findById(artisanId)
                .orElseThrow(() -> new ResourceNotFoundException("Artisan", artisanId));
        artisan.setVerificationStatus(status);
        artisanRepository.save(artisan);
        Map<String, Object> result = new HashMap<>();
        result.put("artisanId", artisan.getArtisanId());
        result.put("fullName", artisan.getFullName());
        result.put("verificationStatus", artisan.getVerificationStatus());
        return ResponseEntity.ok(ApiResponse.success(
                "Artisan status updated to " + status, result));
    }

    @DeleteMapping("/artisans/{artisanId}")
    @Operation(summary = "Remove an artisan from the platform")
    public ResponseEntity<ApiResponse<Void>> deleteArtisan(@PathVariable Long artisanId) {
        if (!artisanRepository.existsById(artisanId))
            throw new ResourceNotFoundException("Artisan", artisanId);
        artisanRepository.deleteById(artisanId);
        return ResponseEntity.ok(ApiResponse.success("Artisan removed", null));
    }

    // ---- Customer Management ----

    @GetMapping("/customers")
    @Operation(summary = "Get all customers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCustomers() {
        List<Map<String, Object>> result = customerRepository.findAll().stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("customerId", c.getCustomerId());
            m.put("fullName", c.getFullName());
            m.put("email", c.getEmail());
            m.put("phone", c.getPhone());
            m.put("isVerified", c.isVerified());
            m.put("createdAt", c.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All customers retrieved", result));
    }

    @DeleteMapping("/customers/{customerId}")
    @Operation(summary = "Remove a customer from the platform")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long customerId) {
        if (!customerRepository.existsById(customerId))
            throw new ResourceNotFoundException("Customer", customerId);
        customerRepository.deleteById(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer removed", null));
    }

    // ---- Order Management ----

    @GetMapping("/orders")
    @Operation(summary = "Get all orders")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllOrders() {
        List<Map<String, Object>> result = orderRepository.findAll().stream().map(o -> {
            Map<String, Object> m = new HashMap<>();
            m.put("orderId", o.getOrderId());
            m.put("customerName", o.getCustomer() != null ? o.getCustomer().getFullName() : "N/A");
            m.put("totalAmount", o.getTotalAmount());
            m.put("status", o.getOrderStatus());
            m.put("orderDate", o.getOrderDate());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All orders retrieved", result));
    }

    // ---- Admin Account Creation ----

    @PostMapping("/create")
    @Operation(summary = "Create another admin account")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createAdmin(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password) {
        if (adminRepository.existsByEmail(email))
            throw new BadRequestException("Admin email already exists: " + email);
        Admin admin = Admin.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        admin = adminRepository.save(admin);
        Map<String, Object> result = new HashMap<>();
        result.put("adminId", admin.getAdminId());
        result.put("email", admin.getEmail());
        result.put("fullName", admin.getFullName());
        return ResponseEntity.ok(ApiResponse.success("Admin account created", result));
    }
}
