package com.korarwanda.kora.entity;

import com.korarwanda.kora.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.ROLE_CUSTOMER;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @Column(name = "verification_code", length = 6)
    private String verificationCode;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    public Boolean getIsVerified() { return isVerified != null && isVerified; }
    public void setIsVerified(Boolean verified) { isVerified = verified; }
    public boolean isVerified() { return getIsVerified(); }
    public void setVerified(boolean verified) { setIsVerified(verified); }

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
