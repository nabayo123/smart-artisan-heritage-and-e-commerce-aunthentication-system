package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.dto.ProductDto;
import com.korarwanda.kora.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Products", description = "Manage artisan products and Heritage Tags")
public class ProductController {

    private final ProductService productService;

    // ---- PUBLIC endpoints (no auth required) ----

    @GetMapping("/api/products/public")
    @Operation(summary = "Get all available products (public)")
    public ResponseEntity<ApiResponse<List<ProductDto.Response>>> getAvailable() {
        return ResponseEntity.ok(ApiResponse.success("Available products retrieved",
                productService.getAvailable()));
    }

    @GetMapping("/api/products/public/{id}")
    @Operation(summary = "Get product by ID (public)")
    public ResponseEntity<ApiResponse<ProductDto.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Product retrieved",
                productService.getById(id)));
    }

    @GetMapping("/api/products/public/search")
    @Operation(summary = "Search products by keyword (public)")
    public ResponseEntity<ApiResponse<List<ProductDto.Response>>> search(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success("Search results",
                productService.search(keyword)));
    }

    // ---- ARTISAN endpoints ----

    @PostMapping("/api/artisan/products")
    @PreAuthorize("hasAnyAuthority('ROLE_ARTISAN', 'ROLE_ADMIN')")
    @Operation(summary = "Create a product with auto-generated Heritage Tag (Artisan)")
    public ResponseEntity<ApiResponse<ProductDto.Response>> create(
            @RequestParam Long artisanId,
            @Valid @RequestBody ProductDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Product created with Heritage Tag", productService.create(artisanId, request)));
    }

    @GetMapping("/api/artisan/products/{artisanId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ARTISAN', 'ROLE_ADMIN')")
    @Operation(summary = "Get all products by artisan")
    public ResponseEntity<ApiResponse<List<ProductDto.Response>>> getByArtisan(
            @PathVariable Long artisanId) {
        return ResponseEntity.ok(ApiResponse.success("Artisan products retrieved",
                productService.getByArtisan(artisanId)));
    }

    @PutMapping("/api/artisan/products/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ARTISAN', 'ROLE_ADMIN')")
    @Operation(summary = "Update a product (Artisan)")
    public ResponseEntity<ApiResponse<ProductDto.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Product updated",
                productService.update(id, request)));
    }


    @DeleteMapping("/api/admin/products/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a product (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
}
