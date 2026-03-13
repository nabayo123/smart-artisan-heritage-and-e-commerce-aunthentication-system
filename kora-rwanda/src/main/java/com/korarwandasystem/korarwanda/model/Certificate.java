package com.korarwandasystem.korarwanda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_number", unique = true, nullable = false)
    @JsonProperty("certificate_number")
    private String certificateNumber;

    @Column(name = "issuing_authority")
    @JsonProperty("issuing_authority")
    private String issuingAuthority;

    @Column(name = "issue_date")
    @JsonProperty("issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    @JsonProperty("expiry_date")
    private LocalDate expiryDate;

    @Column(name = "heritage_description", columnDefinition = "TEXT")
    @JsonProperty("heritage_description")
    private String heritageDescription;

    @Enumerated(EnumType.STRING)
    private CertificateStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_id")
    @JsonProperty("artisan_id")
    private Artisan artisan;

    public Certificate() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getIssuingAuthority() { return issuingAuthority; }
    public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getHeritageDescription() { return heritageDescription; }
    public void setHeritageDescription(String heritageDescription) { this.heritageDescription = heritageDescription; }

    public CertificateStatus getStatus() { return status; }
    public void setStatus(CertificateStatus status) { this.status = status; }

    public Artisan getArtisan() { return artisan; }
    public void setArtisan(Artisan artisan) { this.artisan = artisan; }

    @JsonProperty("artisan_id")
    public void setArtisanId(Long artisanId) {
        if (artisanId != null) {
            if (this.artisan == null) {
                this.artisan = new Artisan();
            }
            this.artisan.setArtisanId(artisanId);
        }
    }
}