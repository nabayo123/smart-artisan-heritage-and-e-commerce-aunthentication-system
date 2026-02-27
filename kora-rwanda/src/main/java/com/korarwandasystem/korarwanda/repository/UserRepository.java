package com.korarwandasystem.korarwanda.repository;

import com.korarwandasystem.korarwanda.model.User;
import com.korarwandasystem.korarwanda.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByEmailAndEnabled(String email, boolean enabled);
    java.util.List<User> findByUserType(UserType userType);
    java.util.List<User> findByEnabled(boolean enabled);
}
