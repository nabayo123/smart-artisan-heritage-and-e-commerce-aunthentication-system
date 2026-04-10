package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Artisan;
import com.korarwanda.kora.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtisanRepository extends JpaRepository<Artisan, Long> {
    Optional<Artisan> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Artisan> findByVerificationStatus(VerificationStatus status);
    List<Artisan> findByCooperative_CooperativeId(Long cooperativeId);
    long countByVerificationStatus(VerificationStatus status);
}
