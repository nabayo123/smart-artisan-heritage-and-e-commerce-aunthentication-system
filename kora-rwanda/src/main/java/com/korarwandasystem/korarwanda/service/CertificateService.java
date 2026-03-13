package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.Certificate;
import com.korarwandasystem.korarwanda.model.CertificateStatus;
import com.korarwandasystem.korarwanda.repository.ArtisanRepository;
import com.korarwandasystem.korarwanda.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ArtisanRepository artisanRepository;

    public CertificateService(CertificateRepository certificateRepository, ArtisanRepository artisanRepository) {
        this.certificateRepository = certificateRepository;
        this.artisanRepository = artisanRepository;
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public Optional<Certificate> getCertificateById(Long id) {
        return certificateRepository.findById(id);
    }

    public Certificate createCertificate(Certificate certificate) {
        if (certificate == null) {
            throw new IllegalArgumentException("Certificate cannot be null");
        }
        return certificateRepository.save(certificate);
    }

    public Certificate updateCertificate(Long id, Certificate certificateDetails) {
        if (certificateDetails == null) {
            throw new IllegalArgumentException("Certificate details cannot be null");
        }
        
        return certificateRepository.findById(id).map(certificate -> {
            if (certificateDetails.getCertificateNumber() != null) {
                certificate.setCertificateNumber(certificateDetails.getCertificateNumber());
            }
            if (certificateDetails.getIssuingAuthority() != null) {
                certificate.setIssuingAuthority(certificateDetails.getIssuingAuthority());
            }
            if (certificateDetails.getIssueDate() != null) {
                certificate.setIssueDate(certificateDetails.getIssueDate());
            }
            if (certificateDetails.getExpiryDate() != null) {
                certificate.setExpiryDate(certificateDetails.getExpiryDate());
            }
            if (certificateDetails.getHeritageDescription() != null) {
                certificate.setHeritageDescription(certificateDetails.getHeritageDescription());
            }
            if (certificateDetails.getStatus() != null) {
                certificate.setStatus(certificateDetails.getStatus());
            }

            if (certificateDetails.getArtisan() != null && certificateDetails.getArtisan().getArtisanId() != null) {
                artisanRepository.findById(certificateDetails.getArtisan().getArtisanId())
                        .ifPresent(certificate::setArtisan);
            }

            return certificateRepository.save(certificate);
        }).orElseThrow(() -> new IllegalArgumentException("Certificate not found with id: " + id));
    }

    public boolean deleteCertificate(Long id) {
        if (certificateRepository.existsById(id)) {
            certificateRepository.deleteById(id);
            return true;
        }
        throw new IllegalArgumentException("Certificate not found with id: " + id);
    }

    public List<Certificate> getCertificatesByArtisanId(Long artisanId) {
        return certificateRepository.findByArtisanArtisanId(artisanId);
    }

    public List<Certificate> getCertificatesByStatus(CertificateStatus status) {
        return certificateRepository.findByStatus(status);
    }

    public List<Certificate> getActiveCertificates() {
        return certificateRepository.findByStatus(CertificateStatus.ACTIVE);
    }

    public List<Certificate> getCertificatesByIssuingAuthority(String authority) {
        return certificateRepository.findByIssuingAuthority(authority);
    }

    public List<Certificate> getCertificatesExpiringBefore(LocalDate date) {
        return certificateRepository.findByExpiryDateBefore(date);
    }

    public List<Certificate> getCertificatesExpiringAfter(LocalDate date) {
        return certificateRepository.findByExpiryDateAfter(date);
    }

    public Optional<Certificate> getCertificateByNumber(String certificateNumber) {
        return certificateRepository.findByCertificateNumber(certificateNumber);
    }

    public long getTotalCertificatesCount() {
        return certificateRepository.count();
    }

    public long getActiveCertificatesCount() {
        return certificateRepository.countByStatus(CertificateStatus.ACTIVE);
    }

    public boolean existsByCertificateNumber(String certificateNumber) {
        return certificateRepository.existsByCertificateNumber(certificateNumber);
    }

    public Certificate renewCertificate(Long certificateId, LocalDate newExpiryDate) {
        return certificateRepository.findById(certificateId).map(certificate -> {
            certificate.setExpiryDate(newExpiryDate);
            certificate.setStatus(CertificateStatus.ACTIVE);
            return certificateRepository.save(certificate);
        }).orElse(null);
    }

    public Certificate revokeCertificate(Long certificateId) {
        return certificateRepository.findById(certificateId).map(certificate -> {
            certificate.setStatus(CertificateStatus.REVOKED);
            return certificateRepository.save(certificate);
        }).orElse(null);
    }
}