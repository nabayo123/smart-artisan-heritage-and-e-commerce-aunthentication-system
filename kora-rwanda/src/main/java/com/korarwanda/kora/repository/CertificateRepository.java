package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByHeritageHash(String heritageHash);
    Optional<Certificate> findByProduct_ProductId(Long productId);
    boolean existsByHeritageHash(String heritageHash);
}
