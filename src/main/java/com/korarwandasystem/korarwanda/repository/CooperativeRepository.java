package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.Cooperative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, Long> {
    
    // Basic CRUD operations inherited from JpaRepository
    
    // Custom queries for cooperative management
    Optional<Cooperative> findByName(String name);
    
    List<Cooperative> findByProvince(String province);
    
    List<Cooperative> findByDistrict(String district);
    
    List<Cooperative> findByProvinceAndDistrict(String province, String district);
    
    boolean existsByName(String name);
    
    boolean existsByContactPhone(String contactPhone);
}
