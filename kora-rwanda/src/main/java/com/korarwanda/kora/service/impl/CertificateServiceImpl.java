package com.korarwanda.kora.service.impl;

import com.korarwanda.kora.dto.CertificateDto;
import com.korarwanda.kora.entity.Artisan;
import com.korarwanda.kora.entity.Certificate;
import com.korarwanda.kora.entity.Product;
import com.korarwanda.kora.enums.CertificateStatus;
import com.korarwanda.kora.exception.ResourceNotFoundException;
import com.korarwanda.kora.repository.CertificateRepository;
import com.korarwanda.kora.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;

    @Override
    public CertificateDto.VerifyResponse verifyByHash(String heritageHash) {
        Certificate cert = certificateRepository.findByHeritageHash(heritageHash).orElse(null);

        CertificateDto.VerifyResponse response = new CertificateDto.VerifyResponse();
        if (cert == null) {
            response.setAuthentic(false);
            response.setMessage("⚠️ WARNING: This Heritage Tag was NOT found in our registry. This item may be counterfeit.");
            return response;
        }

        if (cert.getVerificationStatus() == CertificateStatus.INVALID
                || cert.getVerificationStatus() == CertificateStatus.REVOKED) {
            response.setAuthentic(false);
            response.setMessage("⚠️ WARNING: This Heritage Tag has been revoked or marked invalid.");
            response.setCertificate(toResponse(cert));
            return response;
        }

        response.setAuthentic(true);
        response.setMessage("✅ AUTHENTIC: This is a verified Made-in-Rwanda handcrafted product.");
        response.setCertificate(toResponse(cert));
        return response;
    }

    @Override
    public CertificateDto.Response getByProductId(Long productId) {
        Certificate cert = certificateRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate for product", productId));
        return toResponse(cert);
    }

    private CertificateDto.Response toResponse(Certificate cert) {
        CertificateDto.Response r = new CertificateDto.Response();
        r.setCertificateId(cert.getCertificateId());
        r.setHeritageHash(cert.getHeritageHash());
        r.setQrCodeData(cert.getQrCodeData());
        r.setQrCodeImageUrl(cert.getQrCodeImageUrl());
        r.setIssueDate(cert.getIssueDate());
        r.setVerificationStatus(cert.getVerificationStatus());

        Product product = cert.getProduct();
        if (product != null) {
            r.setProductId(product.getProductId());
            r.setProductName(product.getName());
            Artisan artisan = product.getArtisan();
            if (artisan != null) {
                r.setArtisanName(artisan.getFullName());
                r.setArtisanBio(artisan.getBio());
                r.setArtisanDistrict(artisan.getDistrictVillage());
                if (artisan.getCooperative() != null) {
                    r.setCooperativeName(artisan.getCooperative().getName());
                }
            }
        }
        return r;
    }
}
