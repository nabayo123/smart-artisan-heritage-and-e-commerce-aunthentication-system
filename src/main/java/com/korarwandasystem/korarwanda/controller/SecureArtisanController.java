package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.Artisan;
import com.korarwandasystem.korarwanda.model.VerificationStatus;
import com.korarwandasystem.korarwanda.service.ArtisanService;
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
@RequestMapping("/api/secure/artisans")
public class SecureArtisanController {

    private final ArtisanService artisanService;
    private final AuthService authService;

    public SecureArtisanController(ArtisanService artisanService, AuthService authService) {
        this.artisanService = artisanService;
        this.authService = authService;
    }

    // READ OPERATIONS - Different access levels

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Artisan>> getAllArtisans(Authentication authentication) {
        List<Artisan> artisans = artisanService.getAllArtisans();
        return ResponseEntity.ok(artisans);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<Artisan> getArtisanById(@PathVariable Long id, Authentication authentication) {
        Optional<Artisan> artisanOpt = artisanService.getArtisanById(id);
        if (artisanOpt.isPresent()) {
            return ResponseEntity.ok(artisanOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<Artisan> getMyArtisanProfile(Authentication authentication) {
        // Get current user's artisan profile
        String email = authentication.getName();
        Optional<Artisan> artisanOpt = artisanService.getArtisanByEmail(email);
        if (artisanOpt.isPresent()) {
            return ResponseEntity.ok(artisanOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    // CREATE OPERATIONS - Restricted

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN')")
    public ResponseEntity<Artisan> createArtisan(@Valid @RequestBody Artisan artisan, Authentication authentication) {
        // If ARTISAN role, only allow creating their own profile
        boolean isArtisan = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ARTISAN".equals(authority.getAuthority())) {
                isArtisan = true;
                break;
            }
        }
        
        if (isArtisan) {
            String currentUserEmail = authentication.getName();
            if (!currentUserEmail.equals(artisan.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        Artisan createdArtisan = artisanService.createArtisan(artisan);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArtisan);
    }

    // UPDATE OPERATIONS - Owner or Admin

    @PutMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN')")
    public ResponseEntity<Artisan> updateArtisan(@PathVariable Long id,
                                                 @Valid @RequestBody Artisan artisan,
                                                 Authentication authentication) {
        Optional<Artisan> artisanOpt = artisanService.getArtisanById(id);
        if (artisanOpt.isPresent()) {
            // Additional check for artisans updating their own profile
            boolean isArtisan = false;
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ARTISAN".equals(authority.getAuthority())) {
                    isArtisan = true;
                    break;
                }
            }
            
            if (isArtisan) {
                String currentUserEmail = authentication.getName();
                if (!artisanService.isOwner(id, currentUserEmail)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<Artisan>build();
                }
            }
            return ResponseEntity.ok(artisanService.updateArtisan(id, artisan));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/id/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Artisan> updateArtisanStatus(@PathVariable Long id,
                                                      @RequestParam VerificationStatus status) {
        Optional<Artisan> artisanOpt = artisanService.getArtisanById(id);
        if (artisanOpt.isPresent()) {
            Artisan artisan = artisanOpt.get();
            artisan.setVerificationStatus(status);
            return ResponseEntity.ok(artisanService.updateArtisan(id, artisan));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE OPERATIONS - Admin only

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtisan(@PathVariable Long id) {
        if (artisanService.deleteArtisan(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // FILTERED QUERIES - Role-based access

    @GetMapping("/verified")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Artisan>> getVerifiedArtisans() {
        return ResponseEntity.ok(artisanService.getVerifiedArtisans());
    }

    @GetMapping("/cooperative/{cooperativeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Artisan>> getArtisansByCooperative(@PathVariable Long cooperativeId) {
        return ResponseEntity.ok(artisanService.getArtisansByCooperativeId(cooperativeId));
    }

    @GetMapping("/specialization/{specialization}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Artisan>> getArtisansBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(artisanService.getArtisansBySpecialization(specialization));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Artisan>> getArtisansByStatus(@PathVariable VerificationStatus status) {
        return ResponseEntity.ok(artisanService.getArtisansByVerificationStatus(status));
    }

    // ADMIN OPERATIONS

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalCount() {
        return ResponseEntity.ok(artisanService.getTotalArtisansCount());
    }

    @GetMapping("/verified/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getVerifiedCount() {
        return ResponseEntity.ok(artisanService.getVerifiedArtisansCount());
    }
}
