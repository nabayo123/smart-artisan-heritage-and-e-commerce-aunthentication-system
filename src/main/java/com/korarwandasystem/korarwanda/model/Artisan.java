package com.korarwandasystem.korarwanda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "artisans")
public class Artisan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // standardized ID

    @JsonProperty("full_name")
    private String fullName;

    private String email;
    private String specialization;
    private String province;
    private String district;

    @Enumerated(EnumType.STRING)
    @JsonProperty("verification_status")
    private VerificationStatus verificationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id")
    private Cooperative cooperative;

    // Default constructor
    public Artisan() {}

    // Getters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getSpecialization() { return specialization; }
    public String getProvince() { return province; }
    public String getDistrict() { return district; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public Cooperative getCooperative() { return cooperative; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setProvince(String province) { this.province = province; }
    public void setDistrict(String district) { this.district = district; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    public void setCooperative(Cooperative cooperative) { this.cooperative = cooperative; }

    // JSON mapping for cooperative_id
    @JsonProperty("cooperative_id")
    public void setCooperativeId(Long cooperativeId) {
        if (cooperativeId != null) {
            if (this.cooperative == null) this.cooperative = new Cooperative();
            this.cooperative.setId(cooperativeId); // standardized
        }
    }
}