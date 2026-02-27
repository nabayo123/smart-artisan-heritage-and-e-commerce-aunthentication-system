package com.korarwandasystem.korarwanda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "ARTISAN")
public class Artisan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artisan_id")
    private Long artisanId;

    @Column(name = "full_name", nullable = false, length = 150)
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Column(name = "email", unique = true, length = 150)
    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "district_village", length = 100)
    private String districtVillage;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "momo_number", length = 20)
    private String momoNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id")
    private Cooperative cooperative;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Artisan() {}

    public Artisan(String fullName, String email, String phoneNumber, String districtVillage, 
                   String bio, String momoNumber, Cooperative cooperative) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.districtVillage = districtVillage;
        this.bio = bio;
        this.momoNumber = momoNumber;
        this.cooperative = cooperative;
    }

    // Getters and Setters
    public Long getArtisanId() {
        return artisanId;
    }

    public void setArtisanId(Long artisanId) {
        this.artisanId = artisanId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDistrictVillage() {
        return districtVillage;
    }

    public void setDistrictVillage(String districtVillage) {
        this.districtVillage = districtVillage;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMomoNumber() {
        return momoNumber;
    }

    public void setMomoNumber(String momoNumber) {
        this.momoNumber = momoNumber;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Cooperative getCooperative() {
        return cooperative;
    }

    public void setCooperative(Cooperative cooperative) {
        this.cooperative = cooperative;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
