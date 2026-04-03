package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Cooperative;
import com.korarwanda.kora.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, Long> {
    List<Cooperative> findByProvince(String province);
    List<Cooperative> findByVerificationStatus(VerificationStatus status);
    boolean existsByName(String name);
}
