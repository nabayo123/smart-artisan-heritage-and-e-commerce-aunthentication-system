package com.korarwanda.kora.entity;

import com.korarwanda.kora.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cooperative")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cooperative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cooperativeId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String province;

    @Column(length = 100)
    private String district;

    @Column(length = 20)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "cooperative", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Artisan> artisans;
}
