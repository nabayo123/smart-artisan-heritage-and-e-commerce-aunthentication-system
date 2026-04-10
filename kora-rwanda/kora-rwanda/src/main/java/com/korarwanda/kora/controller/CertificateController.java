package com.korarwanda.kora.controller;

import com.korarwanda.kora.dto.ApiResponse;
import com.korarwanda.kora.dto.CertificateDto;
import com.korarwanda.kora.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Tag(name = "Heritage Authentication", description = "Verify product authenticity using Heritage Tags")
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/verify/{heritageHash}")
    @Operation(summary = "PUBLIC: Verify a product by scanning its Heritage Tag / QR code")
    public ResponseEntity<ApiResponse<CertificateDto.VerifyResponse>> verify(
            @PathVariable String heritageHash) {
        CertificateDto.VerifyResponse response = certificateService.verifyByHash(heritageHash);
        String message = response.isAuthentic()
                ? "Heritage verification successful"
                : "Heritage verification failed - possible counterfeit";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get certificate details by product ID (authenticated users)")
    public ResponseEntity<ApiResponse<CertificateDto.Response>> getByProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Certificate retrieved",
                certificateService.getByProductId(productId)));
    }
}
