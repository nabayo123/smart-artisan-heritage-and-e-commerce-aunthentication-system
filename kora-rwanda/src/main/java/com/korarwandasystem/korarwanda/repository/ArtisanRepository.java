package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Artisan;
import com.korarwandasystem.korarwanda.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtisanRepository extends JpaRepository<Artisan, Long> {
    
    // Basic CRUD operations inherited from JpaRepository
    
    // Custom queries for artisan management
    Optional<Artisan> findByEmail(String email);
    
    List<Artisan> findByVerificationStatus(VerificationStatus status);
    
    List<Artisan> findByCooperative_CooperativeId(Long cooperativeId);
    
    List<Artisan> findByDistrictVillage(String districtVillage);
    
    List<Artisan> findByFullNameContainingIgnoreCase(String fullName);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByMomoNumber(String momoNumber);
    
    @Query("SELECT COUNT(a) FROM Artisan a WHERE a.verificationStatus = :status")
    long countByVerificationStatus(@Param("status") VerificationStatus status);
    
    @Query("SELECT a FROM Artisan a WHERE a.cooperative.province = :province AND a.verificationStatus = :status")
    List<Artisan> findByProvinceAndVerificationStatus(@Param("province") String province, 
                                                      @Param("status") VerificationStatus status);
}
