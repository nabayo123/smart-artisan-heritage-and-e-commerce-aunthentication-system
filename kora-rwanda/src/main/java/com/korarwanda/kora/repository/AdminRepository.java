package com.korarwanda.kora.repository;

import com.korarwanda.kora.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);

    // ── Active-only queries (for login & listing) ──────────────────────
    Optional<Admin> findByEmailAndActiveTrue(String email);
    List<Admin> findAllByActiveTrue();
    long countByActiveTrue();

    // count() (inherited) = ALL admins ever created — used for the hard cap
}
