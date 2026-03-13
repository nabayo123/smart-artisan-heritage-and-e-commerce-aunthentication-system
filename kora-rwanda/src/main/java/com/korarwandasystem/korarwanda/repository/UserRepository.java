package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.User;
import com.korarwandasystem.korarwanda.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByEmailContaining(String email);

    List<User> findByUserType(UserType userType);

    /**
     * This method was missing and caused the "cannot find symbol" error.
     * Spring Data JPA will automatically implement the count logic
     * based on the method name.
     */
    long countByUserType(UserType userType);
}