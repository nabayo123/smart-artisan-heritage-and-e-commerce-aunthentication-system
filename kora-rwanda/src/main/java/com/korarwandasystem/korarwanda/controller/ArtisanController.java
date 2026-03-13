package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.Artisan;
import com.korarwandasystem.korarwanda.model.VerificationStatus;
import com.korarwandasystem.korarwanda.service.ArtisanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artisans")
public class ArtisanController {

    @Autowired
    private ArtisanService artisanService;

    @GetMapping
    public ResponseEntity<List<Artisan>> getAllArtisans() {
        List<Artisan> artisans = artisanService.getAllArtisans();
        return ResponseEntity.ok(artisans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artisan> getArtisanById(@PathVariable Long id) {
        return artisanService.getArtisanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Artisan> createArtisan(@Valid @RequestBody Artisan artisan) {
        Artisan createdArtisan = artisanService.createArtisan(artisan);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArtisan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artisan> updateArtisan(@PathVariable Long id, @Valid @RequestBody Artisan artisan) {
        Artisan updatedArtisan = artisanService.updateArtisan(id, artisan);
        if (updatedArtisan != null) {
            return ResponseEntity.ok(updatedArtisan);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtisan(@PathVariable Long id) {
        boolean deleted = artisanService.deleteArtisan(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cooperative/{cooperativeId}")
    public ResponseEntity<List<Artisan>> getArtisansByCooperativeId(@PathVariable Long cooperativeId) {
        List<Artisan> artisans = artisanService.getArtisansByCooperativeId(cooperativeId);
        return ResponseEntity.ok(artisans);
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Artisan>> getArtisansBySpecialization(@PathVariable String specialization) {
        List<Artisan> artisans = artisanService.getArtisansBySpecialization(specialization);
        return ResponseEntity.ok(artisans);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Artisan>> getArtisansByVerificationStatus(@PathVariable VerificationStatus status) {
        List<Artisan> artisans = artisanService.getArtisansByVerificationStatus(status);
        return ResponseEntity.ok(artisans);
    }

    @GetMapping("/verified")
    public ResponseEntity<List<Artisan>> getVerifiedArtisans() {
        List<Artisan> artisans = artisanService.getVerifiedArtisans();
        return ResponseEntity.ok(artisans);
    }

    @GetMapping("/province/{province}")
    public ResponseEntity<List<Artisan>> getArtisansByProvince(@PathVariable String province) {
        List<Artisan> artisans = artisanService.getArtisansByProvince(province);
        return ResponseEntity.ok(artisans);
    }

    @GetMapping("/district/{district}")
    public ResponseEntity<List<Artisan>> getArtisansByDistrict(@PathVariable String district) {
        List<Artisan> artisans = artisanService.getArtisansByDistrict(district);
        return ResponseEntity.ok(artisans);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalArtisansCount() {
        long count = artisanService.getTotalArtisansCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/verified/count")
    public ResponseEntity<Long> getVerifiedArtisansCount() {
        long count = artisanService.getVerifiedArtisansCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/email/{email}/exists")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = artisanService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
