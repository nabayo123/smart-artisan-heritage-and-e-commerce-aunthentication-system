package com.korarwanda.kora.config;

import com.korarwanda.kora.entity.Admin;
import com.korarwanda.kora.enums.UserRole;
import com.korarwanda.kora.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired private AdminRepository adminRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDefaultAdmin();
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
            log.info("============================================================");
            log.info("  DEFAULT ADMIN ACCOUNT CREATED");
            log.info("  Email   : {}", adminEmail);
            log.info("  Password: Admin@2024");
            log.info("  IMPORTANT: Change this password after first login!");
            log.info("============================================================");
        } else {
            log.info("Admin account already exists - skipping seed.");
        }
    }
}
