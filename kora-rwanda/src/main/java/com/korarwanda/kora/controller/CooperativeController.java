package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.dto.CooperativeDto;
import com.korarwanda.kora.enums.VerificationStatus;
import com.korarwanda.kora.service.CooperativeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cooperatives")
@Tag(name = "Cooperatives", description = "Manage artisan cooperatives")
public class CooperativeController {

    @Autowired
    private CooperativeService cooperativeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new cooperative (Admin only)")
    public ResponseEntity<ApiResponse<CooperativeDto.Response>> create(
            @Valid @RequestBody CooperativeDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Cooperative created",
                cooperativeService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all cooperatives")
    public ResponseEntity<ApiResponse<List<CooperativeDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Cooperatives retrieved",
                cooperativeService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cooperative by ID")
    public ResponseEntity<ApiResponse<CooperativeDto.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Cooperative retrieved",
                cooperativeService.getById(id)));
    }

    @GetMapping("/province/{province}")
    @Operation(summary = "Get cooperatives by province")
    public ResponseEntity<ApiResponse<List<CooperativeDto.Response>>> getByProvince(
            @PathVariable String province) {
        return ResponseEntity.ok(ApiResponse.success("Cooperatives retrieved",
                cooperativeService.getByProvince(province)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update cooperative (Admin only)")
    public ResponseEntity<ApiResponse<CooperativeDto.Response>> update(
            @PathVariable Long id, @Valid @RequestBody CooperativeDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Cooperative updated",
                cooperativeService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update cooperative verification status (Admin only)")
    public ResponseEntity<ApiResponse<CooperativeDto.Response>> updateStatus(
            @PathVariable Long id, @RequestParam VerificationStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                cooperativeService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete cooperative (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        cooperativeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Cooperative deleted", null));
    }
}
