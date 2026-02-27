package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Certificate;
import com.korarwandasystem.korarwanda.model.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    
    // Basic CRUD operations inherited from JpaRepository
    
    // Custom queries for certificate management
    Optional<Certificate> findByHeritageHash(String heritageHash);
    
    Optional<Certificate> findByProduct_ProductId(Long productId);
    
    List<Certificate> findByVerificationStatus(CertificateStatus status);
    
    List<Certificate> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Certificate> findByProduct_Artisan_ArtisanId(Long artisanId);
    
    boolean existsByHeritageHash(String heritageHash);
    
    boolean existsByProduct_ProductId(Long productId);
    
    @Query("SELECT c FROM Certificate c WHERE c.product.artisan.cooperative.province = :province")
    List<Certificate> findByArtisanProvince(@Param("province") String province);
    
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.verificationStatus = :status")
    long countByVerificationStatus(@Param("status") CertificateStatus status);
    
    @Query("SELECT c FROM Certificate c WHERE c.issueDate >= :date")
    List<Certificate> findCertificatesIssuedAfter(@Param("date") LocalDate date);
}
