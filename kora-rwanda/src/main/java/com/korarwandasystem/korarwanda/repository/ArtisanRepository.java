package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Artisan;
import com.korarwandasystem.korarwanda.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtisanRepository extends JpaRepository<Artisan, Long> {
    List<Artisan> findByCooperativeCooperativeId(Long cooperativeId);
    List<Artisan> findBySpecialization(String specialization);
    List<Artisan> findByVerificationStatus(VerificationStatus status);
    List<Artisan> findByProvince(String province);
    List<Artisan> findByDistrict(String district);
    boolean existsByEmail(String email);
    long countByVerificationStatus(VerificationStatus status);
}