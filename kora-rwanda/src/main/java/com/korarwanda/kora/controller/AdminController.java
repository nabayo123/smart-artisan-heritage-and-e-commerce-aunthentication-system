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
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARTISAN')")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Tag(name = "Admin Dashboard", description = "Platform management, oversight and analytics")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ArtisanRepository artisanRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;
    private final CooperativeRepository cooperativeRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.mail.javamail.JavaMailSender mailSender;

    // ---- Dashboard Summary ----

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get platform statistics for the admin dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalArtisans", artisanRepository.count());
        stats.put("pendingArtisans", artisanRepository.countByVerificationStatus(VerificationStatus.PENDING));
        stats.put("approvedArtisans", artisanRepository.countByVerificationStatus(VerificationStatus.APPROVED));
        stats.put("totalProducts", productRepository.count());
        stats.put("pendingProducts", productRepository.countByStatus(com.korarwanda.kora.enums.ProductStatus.FLAGGED));
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalCooperatives", cooperativeRepository.count());
        stats.put("pendingCooperatives", cooperativeRepository.countByVerificationStatus(VerificationStatus.PENDING));
        
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
    @Transactional
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

    @PutMapping("/artisans/{artisanId}")
    @Operation(summary = "Update artisan profile details")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> updateArtisan(
            @PathVariable Long artisanId,
            @RequestBody Map<String, Object> request) {
        Artisan artisan = artisanRepository.findById(artisanId)
                .orElseThrow(() -> new ResourceNotFoundException("Artisan", artisanId));
        
        if (request.containsKey("fullName")) artisan.setFullName((String) request.get("fullName"));
        if (request.containsKey("districtVillage")) artisan.setDistrictVillage((String) request.get("districtVillage"));
        if (request.containsKey("bio")) artisan.setBio((String) request.get("bio"));
        if (request.containsKey("phoneNumber")) artisan.setPhoneNumber((String) request.get("phoneNumber"));
        
        artisanRepository.save(artisan);
        return ResponseEntity.ok(ApiResponse.success("Artisan profile updated", null));
    }

    @DeleteMapping("/artisans/{artisanId}")
    @Operation(summary = "Remove an artisan from the platform")
    @Transactional
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
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long customerId) {
        if (!customerRepository.existsById(customerId))
            throw new ResourceNotFoundException("Customer", customerId);
        customerRepository.deleteById(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer removed", null));
    }

    @PutMapping("/customers/{customerId}")
    @Operation(summary = "Update customer details")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> request) {
        com.korarwanda.kora.entity.Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        
        if (request.containsKey("fullName")) customer.setFullName((String) request.get("fullName"));
        if (request.containsKey("phone")) customer.setPhone((String) request.get("phone"));
        
        customer = customerRepository.save(customer);
        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customer.getCustomerId());
        result.put("fullName", customer.getFullName());
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", result));
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

    @PatchMapping("/orders/{orderId}/status")
    @Operation(summary = "Update order status")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam com.korarwanda.kora.enums.OrderStatus status) {
        com.korarwanda.kora.entity.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        order.setOrderStatus(status);
        orderRepository.save(order);
        return ResponseEntity.ok(ApiResponse.success("Order status updated to " + status, null));
    }

    @DeleteMapping("/orders/{orderId}")
    @Operation(summary = "Delete an order record")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        if (!orderRepository.existsById(orderId))
            throw new ResourceNotFoundException("Order", orderId);
        orderRepository.deleteById(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order record deleted", null));
    }

    // ---- Admin Account Creation ----

    @PostMapping("/create")
    @Operation(summary = "Create another admin account")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> createAdmin(
            @RequestBody Map<String, String> request) {
        String fullName = request.get("fullName");
        String email = request.get("email");
        String password = request.get("password");

        if (adminRepository.existsByEmail(email))
            throw new BadRequestException("Admin email already exists: " + email);
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        log.info("📧 Sending verification code to new Admin {}: {}", email, code);
        
        Admin admin = Admin.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .verificationCode(code)
                .isVerified(false)
                .build();
        admin = adminRepository.save(admin);

        // Send email
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("Kora-Rwanda Admin <nabayoclementine@gmail.com>");
            helper.setTo(email);
            helper.setSubject("Kora-Rwanda - Admin Access Verification");
            String htmlContent = String.format(
                "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "    <h2 style=\"color: #2e7d32;\">New Admin Account Created</h2>" +
                "    <p>An administrative account has been created for you. Use the code below to verify your access:</p>" +
                "    <div style=\"background: #f1f8e9; padding: 15px; font-size: 24px; font-weight: bold; color: #2e7d32;\">%s</div>" +
                "</div>", code);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Verification email sent to new Admin: {}", email);
        } catch (Exception e) {
            log.error("Failed to send Admin verification email", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("adminId", admin.getAdminId());
        result.put("email", admin.getEmail());
        result.put("fullName", admin.getFullName());
        return ResponseEntity.ok(ApiResponse.success("Admin account created", result));
    }

    @GetMapping("/list")
    @Operation(summary = "Get all platform administrators")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllAdmins() {
        List<Map<String, Object>> result = adminRepository.findAll().stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("adminId", a.getAdminId());
            m.put("fullName", a.getFullName());
            m.put("email", a.getEmail());
            m.put("role", a.getRole());
            m.put("createdAt", a.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All admins retrieved", result));
    }

    @PostMapping("/customers")
    @Operation(summary = "Admin: Manually register a new customer")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminCreateCustomer(
            @Valid @RequestBody com.korarwanda.kora.dto.AuthDto.CustomerRegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Customer email already exists: " + request.getEmail());
        
        com.korarwanda.kora.entity.Customer customer = com.korarwanda.kora.entity.Customer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .isVerified(true) // Admin creation bypasses email verification
                .build();
        
        customer = customerRepository.save(customer);
        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customer.getCustomerId());
        result.put("fullName", customer.getFullName());
        return ResponseEntity.ok(ApiResponse.success("Customer manually created by admin", result));
    }

    // ---- Payment Management ----

    @GetMapping("/payments")
    @Operation(summary = "Get all payments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllPayments() {
        List<Map<String, Object>> result = paymentRepository.findAll().stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("paymentId", p.getPaymentId());
            m.put("orderId", p.getOrder() != null ? p.getOrder().getOrderId() : null);
            m.put("paymentMethod", p.getPaymentMethod());
            m.put("status", p.getPaymentStatus());
            m.put("transactionRef", p.getTransactionRef());
            m.put("amount", p.getAmount());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All payments retrieved", result));
    }

    @DeleteMapping("/payments/{paymentId}")
    @Operation(summary = "Delete a payment record")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable Long paymentId) {
        if (!paymentRepository.existsById(paymentId))
            throw new ResourceNotFoundException("Payment", paymentId);
        paymentRepository.deleteById(paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment record deleted", null));
    }

    // ---- Product Management (Mirrored for /api/admin route) ----

    @GetMapping("/products")
    @Operation(summary = "Get all products for management")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllProducts() {
        List<Map<String, Object>> result = productRepository.findAll().stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("productId", p.getProductId());
            m.put("name", p.getName());
            m.put("artisanName", p.getArtisan() != null ? p.getArtisan().getFullName() : "N/A");
            m.put("category", p.getCategory());
            m.put("price", p.getPrice());
            m.put("status", p.getStatus());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All products retrieved", result));
    }

    @PutMapping("/products/{productId}")
    @Operation(summary = "Update product details")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request) {
        com.korarwanda.kora.entity.Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        
        if (request.containsKey("name")) product.setName((String) request.get("name"));
        if (request.containsKey("category")) product.setCategory((String) request.get("category"));
        if (request.containsKey("price")) product.setPrice(new java.math.BigDecimal(request.get("price").toString()));
        if (request.containsKey("status")) product.setStatus(com.korarwanda.kora.enums.ProductStatus.valueOf((String) request.get("status")));
        
        productRepository.save(product);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", null));
    }
    
    // ---- Cooperative Management ----

    @GetMapping("/cooperatives")
    @Operation(summary = "Get all cooperatives")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCooperatives() {
        List<Map<String, Object>> result = cooperativeRepository.findAll().stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("cooperativeId", c.getCooperativeId());
            m.put("name", c.getName());
            m.put("province", c.getProvince());
            m.put("district", c.getDistrict());
            m.put("contactPhone", c.getContactPhone());
            m.put("verificationStatus", c.getVerificationStatus());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All cooperatives retrieved", result));
    }

    @PutMapping("/cooperatives/{cooperativeId}")
    @Operation(summary = "Update cooperative details")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> updateCooperative(
            @PathVariable Long cooperativeId,
            @RequestBody Map<String, Object> request) {
        com.korarwanda.kora.entity.Cooperative coop = cooperativeRepository.findById(cooperativeId)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", cooperativeId));
        
        if (request.containsKey("name")) coop.setName((String) request.get("name"));
        if (request.containsKey("province")) coop.setProvince((String) request.get("province"));
        if (request.containsKey("district")) coop.setDistrict((String) request.get("district"));
        if (request.containsKey("contactPhone")) coop.setContactPhone((String) request.get("contactPhone"));
        if (request.containsKey("verificationStatus")) coop.setVerificationStatus(com.korarwanda.kora.enums.VerificationStatus.valueOf((String) request.get("verificationStatus")));
        
        cooperativeRepository.save(coop);
        return ResponseEntity.ok(ApiResponse.success("Cooperative updated successfully", null));
    }

    @DeleteMapping("/cooperatives/{cooperativeId}")
    @Operation(summary = "Delete cooperative")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteCooperative(@PathVariable Long cooperativeId) {
        if (!cooperativeRepository.existsById(cooperativeId))
            throw new ResourceNotFoundException("Cooperative", cooperativeId);
        cooperativeRepository.deleteById(cooperativeId);
        return ResponseEntity.ok(ApiResponse.success("Cooperative deleted", null));
    }
}
