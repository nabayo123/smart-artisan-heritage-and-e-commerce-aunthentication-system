package com.korarwanda.kora.entity;

import com.korarwanda.kora.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.ROLE_ADMIN;

    /**
     * Soft-delete flag.
     * Once the system has 3 admins (active OR inactive), no new admin
     * can ever be created.  "Deleting" an admin only sets this to false
     * so the lifetime count is preserved.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "verification_code", length = 10)
    private String verificationCode;

    @Column(nullable = false)
    @Builder.Default
    private boolean isVerified = false;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}
