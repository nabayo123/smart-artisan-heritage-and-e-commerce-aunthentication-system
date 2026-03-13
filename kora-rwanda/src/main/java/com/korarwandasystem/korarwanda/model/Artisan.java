package com.korarwandasystem.korarwanda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "artisans")
public class Artisan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("artisan_id")
    private Long artisanId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("specialization")
    private String specialization;

    @JsonProperty("province")
    private String province;

    @JsonProperty("district")
    private String district;

    @Enumerated(EnumType.STRING)
    @JsonProperty("verification_status")
    private VerificationStatus verificationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id")
    @JsonProperty("cooperative")
    private Cooperative cooperative;

    // Default Constructor
    public Artisan() {}

    // GETTERS (These resolve the "cannot find symbol" errors)
    public Long getArtisanId() { return artisanId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getSpecialization() { return specialization; }
    public String getProvince() { return province; }
    public String getDistrict() { return district; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public Cooperative getCooperative() { return cooperative; }

    // SETTERS (Needed for creating and updating artisans)
    public void setArtisanId(Long artisanId) { this.artisanId = artisanId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setProvince(String province) { this.province = province; }
    public void setDistrict(String district) { this.district = district; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    public void setCooperative(Cooperative cooperative) { this.cooperative = cooperative; }

    @JsonProperty("cooperative_id")
    public void setCooperativeId(Long cooperativeId) {
        if (cooperativeId != null) {
            if (this.cooperative == null) {
                this.cooperative = new Cooperative();
            }
            this.cooperative.setCooperativeId(cooperativeId);
        }
    }
}