package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findFirstByEmailOrderByCreatedAtDesc(String email);
    
    default Optional<Admin> findByEmail(String email) {
        return findFirstByEmailOrderByCreatedAtDesc(email);
    }
    
    boolean existsByEmail(String email);

    // ── Active-only queries (for login & listing) ──────────────────────
    Optional<Admin> findFirstByEmailAndActiveTrueOrderByCreatedAtDesc(String email);
    
    default Optional<Admin> findByEmailAndActiveTrue(String email) {
        return findFirstByEmailAndActiveTrueOrderByCreatedAtDesc(email);
    }
    List<Admin> findAllByActiveTrue();
    long countByActiveTrue();

    // count() (inherited) = ALL admins ever created — used for the hard cap
}
