package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.Certificate;
import com.korarwandasystem.korarwanda.model.CertificateStatus;
import com.korarwandasystem.korarwanda.service.CertificateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping
    public ResponseEntity<List<Certificate>> getAllCertificates() {
        List<Certificate> certificates = certificateService.getAllCertificates();
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificateById(@PathVariable Long id) {
        return certificateService.getCertificateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Certificate> createCertificate(@Valid @RequestBody Certificate certificate) {
        Certificate createdCertificate = certificateService.createCertificate(certificate);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Certificate> updateCertificate(@PathVariable Long id, @Valid @RequestBody Certificate certificate) {
        Certificate updatedCertificate = certificateService.updateCertificate(id, certificate);
        if (updatedCertificate != null) {
            return ResponseEntity.ok(updatedCertificate);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        boolean deleted = certificateService.deleteCertificate(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/artisan/{artisanId}")
    public ResponseEntity<List<Certificate>> getCertificatesByArtisanId(@PathVariable Long artisanId) {
        List<Certificate> certificates = certificateService.getCertificatesByArtisanId(artisanId);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Certificate>> getCertificatesByStatus(@PathVariable CertificateStatus status) {
        List<Certificate> certificates = certificateService.getCertificatesByStatus(status);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Certificate>> getActiveCertificates() {
        List<Certificate> certificates = certificateService.getActiveCertificates();
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/authority/{authority}")
    public ResponseEntity<List<Certificate>> getCertificatesByIssuingAuthority(@PathVariable String authority) {
        List<Certificate> certificates = certificateService.getCertificatesByIssuingAuthority(authority);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/expiring-before")
    public ResponseEntity<List<Certificate>> getCertificatesExpiringBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Certificate> certificates = certificateService.getCertificatesExpiringBefore(date);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/expiring-after")
    public ResponseEntity<List<Certificate>> getCertificatesExpiringAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Certificate> certificates = certificateService.getCertificatesExpiringAfter(date);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/number/{certificateNumber}")
    public ResponseEntity<Certificate> getCertificateByNumber(@PathVariable String certificateNumber) {
        return certificateService.getCertificateByNumber(certificateNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCertificatesCount() {
        long count = certificateService.getTotalCertificatesCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> getActiveCertificatesCount() {
        long count = certificateService.getActiveCertificatesCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/number/{certificateNumber}/exists")
    public ResponseEntity<Boolean> checkCertificateNumberExists(@PathVariable String certificateNumber) {
        boolean exists = certificateService.existsByCertificateNumber(certificateNumber);
        return ResponseEntity.ok(exists);
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<Certificate> renewCertificate(
            @PathVariable Long id, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newExpiryDate) {
        Certificate renewedCertificate = certificateService.renewCertificate(id, newExpiryDate);
        if (renewedCertificate != null) {
            return ResponseEntity.ok(renewedCertificate);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/revoke")
    public ResponseEntity<Certificate> revokeCertificate(@PathVariable Long id) {
        Certificate revokedCertificate = certificateService.revokeCertificate(id);
        if (revokedCertificate != null) {
            return ResponseEntity.ok(revokedCertificate);
        }
        return ResponseEntity.notFound().build();
    }
}
