package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.entity.Artisan;
import com.korarwanda.kora.exception.ResourceNotFoundException;
import com.korarwanda.kora.repository.ArtisanRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artisan")
@RequiredArgsConstructor
@Tag(name = "Artisan Profile", description = "Manage artisan profile and verification status")
public class ArtisanController {

    private final ArtisanRepository artisanRepository;

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAnyRole('ARTISAN', 'ADMIN')")
    @Operation(summary = "Get artisan profile details")
    public ResponseEntity<ApiResponse<Artisan>> getProfile(@PathVariable Long id) {
        Artisan artisan = artisanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artisan", id));
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", artisan));
    }
}
