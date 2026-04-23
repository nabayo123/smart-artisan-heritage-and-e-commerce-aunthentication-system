package com.korarwanda.kora.config;

import com.korarwanda.kora.entity.Admin;
import com.korarwanda.kora.entity.Cooperative;
import com.korarwanda.kora.enums.UserRole;
import com.korarwanda.kora.enums.VerificationStatus;
import com.korarwanda.kora.repository.AdminRepository;
import com.korarwanda.kora.repository.CooperativeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AdminRepository adminRepository;
    private final CooperativeRepository cooperativeRepository;
    private final com.korarwanda.kora.repository.ArtisanRepository artisanRepository;
    private final com.korarwanda.kora.repository.CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDefaultAdmin();
        seedInitialCooperatives();
        seedDefaultArtisan();
        seedDefaultCustomer();
    }

    private void seedInitialCooperatives() {
        if (cooperativeRepository.count() == 0) {
            log.info("Seeding initial Cooperatives...");
            
            Cooperative c1 = Cooperative.builder()
                .name("Kigali Weaver's Union")
                .province("Kigali City")
                .district("Nyarugenge")
                .contactPhone("+250788123456")
                .verificationStatus(VerificationStatus.APPROVED)
                .build();
            
            Cooperative c2 = Cooperative.builder()
                .name("Lake Kivu Arts & Crafts")
                .province("Western Province")
                .district("Rubavu")
                .contactPhone("+250788654321")
                .verificationStatus(VerificationStatus.APPROVED)
                .build();
            
            Cooperative c3 = Cooperative.builder()
                .name("Northern Highland Potters")
                .province("Northern Province")
                .district("Musanze")
                .contactPhone("+250788777888")
                .verificationStatus(VerificationStatus.PENDING)
                .build();
                
            cooperativeRepository.saveAll(java.util.List.of(c1, c2, c3));
            log.info("3 Cooperatives seeded successfully.");
        }
    }

    private void seedDefaultAdmin() {
        String adminEmail = "admin@kora-rwanda.rw";
        if (!adminRepository.existsByEmail(adminEmail)) {
            Admin admin = Admin.builder()
                    .fullName("Kora Rwanda Administrator")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@2024"))
                    .role(UserRole.ROLE_ADMIN)
                    .build();
            adminRepository.save(admin);
            log.info("DEFAULT ADMIN ACCOUNT CREATED: " + adminEmail);
        } else {
            Admin admin = adminRepository.findByEmail(adminEmail).get();
            admin.setPassword(passwordEncoder.encode("Admin@2024"));
            adminRepository.save(admin);
            log.info("Admin account already exists - forced password reset to Admin@2024 for testing.");
        }
    }
    private void seedDefaultArtisan() {
        String email = "artisan@test.rw";
        if (artisanRepository.count() == 0 && !artisanRepository.existsByEmail(email)) {
            com.korarwanda.kora.entity.Artisan a = com.korarwanda.kora.entity.Artisan.builder()
                .fullName("Uwimana Marie")
                .email(email)
                .password(passwordEncoder.encode("Test@123"))
                .role(UserRole.ROLE_ARTISAN)
                .verificationStatus(VerificationStatus.APPROVED)
                .isVerified(true)
                .verificationCode("123456")
                .build();
            artisanRepository.save(a);
            log.info("Default Artisan created: {} / Test@123", email);
        }
    }

    private void seedDefaultCustomer() {
        String email = "customer@test.rw";
        if (customerRepository.count() == 0 && !customerRepository.existsByEmail(email)) {
            com.korarwanda.kora.entity.Customer c = com.korarwanda.kora.entity.Customer.builder()
                .fullName("Buyer John")
                .email(email)
                .password(passwordEncoder.encode("Test@123"))
                .role(UserRole.ROLE_CUSTOMER)
                .isVerified(true)
                .verificationCode("123456")
                .build();
            customerRepository.save(c);
            log.info("Default Customer created: {} / Test@123", email);
        }
    }
}
