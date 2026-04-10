package com.korarwanda.kora.service;

import com.korarwanda.kora.dto.CertificateDto;

public interface CertificateService {
    CertificateDto.VerifyResponse verifyByHash(String heritageHash);
    CertificateDto.Response getByProductId(Long productId);
}
