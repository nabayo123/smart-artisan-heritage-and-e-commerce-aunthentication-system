package com.korarwandasystem.korarwanda.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COOPERATIVE")
public class Cooperative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cooperative_id")
    private Long cooperativeId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Cooperative() {}

    public Cooperative(String name, String province, String district, String contactPhone) {
        this.name = name;
        this.province = province;
        this.district = district;
        this.contactPhone = contactPhone;
    }

    // Getters and Setters
    public Long getCooperativeId() {
        return cooperativeId;
    }

    public void setCooperativeId(Long cooperativeId) {
        this.cooperativeId = cooperativeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
