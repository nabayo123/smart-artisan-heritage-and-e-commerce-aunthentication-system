package com.korarwanda.kora.entity;

import com.korarwanda.kora.enums.UserRole;
import com.korarwanda.kora.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "artisan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Artisan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artisanId;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(length = 20)
    private String phoneNumber;

    @Column(name = "district_village", length = 100)
    private String districtVillage;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "momo_number", length = 20)
    private String momoNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.ROLE_ARTISAN;

    @Column(name = "proof_video_url", length = 500)
    private String proofVideoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id")
    private Cooperative cooperative;

    @OneToMany(mappedBy = "artisan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
