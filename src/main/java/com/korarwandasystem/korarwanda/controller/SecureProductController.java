package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.Product;
import com.korarwandasystem.korarwanda.model.ProductStatus;
import com.korarwandasystem.korarwanda.service.ProductService;
import com.korarwandasystem.korarwanda.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/secure/products")
public class SecureProductController {

    private final ProductService productService;
    private final AuthService authService;

    public SecureProductController(ProductService productService, AuthService authService) {
        this.productService = productService;
        this.authService = authService;
    }

    // READ OPERATIONS - Different access levels

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Product>> getAllProducts(Authentication authentication) {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            return ResponseEntity.ok(productOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<List<Product>> getMyProducts(Authentication authentication) {
        // Get current artisan's products
        String email = authentication.getName();
        return ResponseEntity.ok(productService.getProductsByArtisanEmail(email));
    }

    @GetMapping("/artisan/{artisanId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Product>> getProductsByArtisan(@PathVariable Long artisanId) {
        return ResponseEntity.ok(productService.getProductsByArtisanId(artisanId));
    }

    // CREATE OPERATIONS - Artisan or Admin

    @PostMapping
    @PreAuthorize("hasRole('ARTISAN') or hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, Authentication authentication) {
        // If ARTISAN role, ensure they're creating for their own artisan account
        boolean isArtisan = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ARTISAN".equals(authority.getAuthority())) {
                isArtisan = true;
                break;
            }
        }

        if (isArtisan) {
            String currentUserEmail = authentication.getName();
            // Set the artisan based on current user
            product.setArtisan(productService.getArtisanByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Artisan profile not found")));
        }

        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // UPDATE OPERATIONS - Owner or Admin

    @PutMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @Valid @RequestBody Product product,
                                                 Authentication authentication) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            // Additional check for artisans updating their own products
            boolean isArtisan = false;
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ARTISAN".equals(authority.getAuthority())) {
                    isArtisan = true;
                    break;
                }
            }

            if (isArtisan) {
                if (!productService.isOwner(id, authentication.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<Product>build();
                }
            }
            return ResponseEntity.ok(productService.updateProduct(id, product));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/id/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN')")
    public ResponseEntity<Product> updateProductStatus(@PathVariable Long id,
                                                      @RequestParam ProductStatus status,
                                                      Authentication authentication) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            // Check ownership for artisans
            boolean isArtisan = false;
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ARTISAN".equals(authority.getAuthority())) {
                    isArtisan = true;
                    break;
                }
            }

            if (isArtisan) {
                if (!productService.isOwner(id, authentication.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<Product>build();
                }
            }
            product.setStatus(status);
            return ResponseEntity.ok(productService.updateProduct(id, product));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/id/{id}/price")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN')")
    public ResponseEntity<Product> updateProductPrice(@PathVariable Long id,
                                                       @RequestParam Double price,
                                                       Authentication authentication) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            // Check ownership for artisans
            boolean isArtisan = false;
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ARTISAN".equals(authority.getAuthority())) {
                    isArtisan = true;
                    break;
                }
            }

            if (isArtisan) {
                if (!productService.isOwner(id, authentication.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<Product>build();
                }
            }
            product.setPrice(java.math.BigDecimal.valueOf(price));
            return ResponseEntity.ok(productService.updateProduct(id, product));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE OPERATIONS - Owner or Admin

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Authentication authentication) {
        // Check ownership for artisans
        boolean isArtisan = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ARTISAN".equals(authority.getAuthority())) {
                isArtisan = true;
                break;
            }
        }

        if (isArtisan) {
            if (!productService.isOwner(id, authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // FILTERED QUERIES - Role-based access

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getProductsByStatus(ProductStatus.AVAILABLE));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/price-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Product>> getProductsByPriceRange(@RequestParam Double min,
                                                                 @RequestParam Double max) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(
            java.math.BigDecimal.valueOf(min),
            java.math.BigDecimal.valueOf(max)
        ));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    // ADMIN OPERATIONS

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalCount() {
        return ResponseEntity.ok(productService.getTotalProductsCount());
    }

    @GetMapping("/available/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getAvailableCount() {
        return ResponseEntity.ok((long) productService.getProductsByStatus(ProductStatus.AVAILABLE).size());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getProductsByStatus(@PathVariable ProductStatus status) {
        return ResponseEntity.ok(productService.getProductsByStatus(status));
    }
}
