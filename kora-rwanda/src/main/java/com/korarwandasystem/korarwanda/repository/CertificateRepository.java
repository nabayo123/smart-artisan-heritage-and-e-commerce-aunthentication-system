package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Certificate;
import com.korarwandasystem.korarwanda.model.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByArtisanArtisanId(Long artisanId);

    List<Certificate> findByStatus(CertificateStatus status);

    List<Certificate> findByIssuingAuthority(String authority);

    List<Certificate> findByExpiryDateBefore(LocalDate date);

    List<Certificate> findByExpiryDateAfter(LocalDate date);

    Optional<Certificate> findByCertificateNumber(String certificateNumber);

    long countByStatus(CertificateStatus status);

    boolean existsByCertificateNumber(String certificateNumber);
}