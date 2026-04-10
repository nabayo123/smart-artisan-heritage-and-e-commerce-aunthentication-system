package com.korarwanda.kora.entity;

import com.korarwanda.kora.enums.CertificateStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "certificate")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    private Product product;

    @Column(name = "heritage_hash", unique = true, nullable = false, length = 255)
    private String heritageHash;

    @Column(name = "qr_code_data", columnDefinition = "LONGTEXT")
    private String qrCodeData;
 
    @Column(name = "qr_code_image_url", columnDefinition = "LONGTEXT")
    private String qrCodeImageUrl;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CertificateStatus verificationStatus = CertificateStatus.VALID;
}
