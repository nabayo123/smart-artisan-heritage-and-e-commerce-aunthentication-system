package com.korarwanda.kora.service.impl;

import com.korarwanda.kora.dto.AuthDto;
import com.korarwanda.kora.entity.Admin;
import com.korarwanda.kora.entity.Artisan;
import com.korarwanda.kora.entity.Cooperative;
import com.korarwanda.kora.entity.Customer;
import com.korarwanda.kora.enums.UserRole;
import com.korarwanda.kora.exception.BadRequestException;
import com.korarwanda.kora.exception.ResourceNotFoundException;
import com.korarwanda.kora.repository.AdminRepository;
import com.korarwanda.kora.repository.ArtisanRepository;
import com.korarwanda.kora.repository.CooperativeRepository;
import com.korarwanda.kora.repository.CustomerRepository;
import com.korarwanda.kora.security.JwtUtils;
import com.korarwanda.kora.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;

// Removed unused Optional import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final ArtisanRepository artisanRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final CooperativeRepository cooperativeRepository;
    private final JavaMailSender mailSender;

    private void checkEmailUniqueness(String email) {
        if (adminRepository.existsByEmail(email) || 
            artisanRepository.existsByEmail(email) || 
            customerRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered on the platform: " + email);
        }
    }

    @Override
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        String role = request.getRole().toUpperCase();
        return switch (role) {
            case "ARTISAN" -> {
                Artisan a = artisanRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new BadRequestException("This account is not registered as an Artisan."));
                if (!a.isVerified()) throw new BadRequestException("Account not verified. Please use your verification code.");
                yield new AuthDto.LoginResponse(jwt, a.getEmail(), a.getFullName(), a.getRole(), a.getArtisanId());
            }
            case "CUSTOMER" -> {
                Customer c = customerRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new BadRequestException("This account is not registered as a Customer."));
                if (!c.isVerified()) throw new BadRequestException("Account not verified. Please use your verification code.");
                yield new AuthDto.LoginResponse(jwt, c.getEmail(), c.getFullName(), c.getRole(), c.getCustomerId());
            }
            case "ADMIN" -> {
                Admin adm = adminRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new BadRequestException("This account is not registered as an Admin."));
                yield new AuthDto.LoginResponse(jwt, adm.getEmail(), adm.getFullName(), adm.getRole(), adm.getAdminId());
            }
            default -> throw new BadRequestException("Invalid role selected.");
        };
    }

    @Override
    public AuthDto.LoginResponse registerArtisan(AuthDto.ArtisanRegisterRequest request) {
        checkEmailUniqueness(request.getEmail());

        String code = generateAndSendCode(request.getEmail(), "ARTISAN");

        Artisan artisan = Artisan.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .districtVillage(request.getDistrictVillage())
                .bio(request.getBio())
                .momoNumber(request.getMomoNumber())
                .role(UserRole.ROLE_ARTISAN)
                .verificationCode(code)
                .isVerified(true)
                .verificationStatus(com.korarwanda.kora.enums.VerificationStatus.APPROVED)
                .build();

        if (request.getCooperativeId() != null) {
            Cooperative cooperative = cooperativeRepository.findById(request.getCooperativeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cooperative", request.getCooperativeId()));
            artisan.setCooperative(cooperative);
        }

        artisan = artisanRepository.save(artisan);

        return new AuthDto.LoginResponse(null, artisan.getEmail(), artisan.getFullName(),
                artisan.getRole(), artisan.getArtisanId());
    }

    @Override
    public AuthDto.LoginResponse registerCustomer(AuthDto.CustomerRegisterRequest request) {
        checkEmailUniqueness(request.getEmail());

        String code = generateAndSendCode(request.getEmail(), "CUSTOMER");

        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(UserRole.ROLE_CUSTOMER)
                .verificationCode(code)
                .isVerified(false)
                .build();

        customer = customerRepository.save(customer);

        return new AuthDto.LoginResponse(null, customer.getEmail(), customer.getFullName(),
                customer.getRole(), customer.getCustomerId());
    }

    private String generateAndSendCode(String email, String label) {
        String code = String.format("%06d", new Random().nextInt(999999));
        log.info("📧 Sending verification code to {} ({}): {}", email, label, code);
        sendVerificationEmail(email, code);
        return code;
    }

    @Override
    public AuthDto.LoginResponse registerAdmin(AuthDto.AdminRegisterRequest request) {
        checkEmailUniqueness(request.getEmail());

        String code = generateAndSendCode(request.getEmail(), "ADMIN");

        Admin admin = Admin.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_ADMIN)
                .verificationCode(code)
                .isVerified(false)
                .build();

        admin = adminRepository.save(admin);

        return new AuthDto.LoginResponse(null, admin.getEmail(), admin.getFullName(),
                admin.getRole(), admin.getAdminId());
    }



    @Override
    public void updatePassword(AuthDto.PasswordUpdateRequest request) {
        String role = request.getRole().toUpperCase();
        switch (role) {
            case "ARTISAN" -> {
                Artisan a = artisanRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Artisan", 0L));
                if (!passwordEncoder.matches(request.getCurrentPassword(), a.getPassword())) {
                    throw new BadRequestException("Current password incorrect");
                }
                a.setPassword(passwordEncoder.encode(request.getNewPassword()));
                artisanRepository.save(a);
            }
            case "CUSTOMER" -> {
                Customer c = customerRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", 0L));
                if (!passwordEncoder.matches(request.getCurrentPassword(), c.getPassword())) {
                    throw new BadRequestException("Current password incorrect");
                }
                c.setPassword(passwordEncoder.encode(request.getNewPassword()));
                customerRepository.save(c);
            }
            case "ADMIN" -> {
                Admin adm = adminRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Admin", 0L));
                if (!passwordEncoder.matches(request.getCurrentPassword(), adm.getPassword())) {
                    throw new BadRequestException("Current password incorrect");
                }
                adm.setPassword(passwordEncoder.encode(request.getNewPassword()));
                adminRepository.save(adm);
            }
            default -> throw new BadRequestException("Invalid role");
        }
    }

    @Override
    public void verifyAccount(AuthDto.VerifyAccountRequest request) {
        String role = request.getRole().toUpperCase();
        switch (role) {
            case "ARTISAN" -> {
                Artisan a = artisanRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Artisan", 0L));
                if (a.isVerified()) throw new BadRequestException("Account already verified");
                if (request.getCode().equals(a.getVerificationCode())) {
                    a.setVerified(true);
                    a.setVerificationCode(null);
                    artisanRepository.save(a);
                } else {
                    throw new BadRequestException("Invalid verification code");
                }
            }
            case "CUSTOMER" -> {
                Customer c = customerRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", 0L));
                if (c.isVerified()) throw new BadRequestException("Account already verified");
                if (request.getCode().equals(c.getVerificationCode())) {
                    c.setVerified(true);
                    c.setVerificationCode(null);
                    customerRepository.save(c);
                } else {
                    throw new BadRequestException("Invalid verification code");
                }
            }
            case "ADMIN" -> {
                Admin adm = adminRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Admin", 0L));
                if (adm.isVerified()) throw new BadRequestException("Account already verified");
                if (request.getCode().equals(adm.getVerificationCode())) {
                    adm.setVerified(true);
                    adm.setVerificationCode(null);
                    adminRepository.save(adm);
                } else {
                    throw new BadRequestException("Invalid verification code");
                }
            }
            default -> throw new BadRequestException("Invalid role for verification");
        }
    }

    @Override
    public void resendVerificationCode(AuthDto.ResendVerificationRequest request) {
        String role = request.getRole().toUpperCase();
        String email = request.getEmail();
        String code = String.format("%06d", new java.util.Random().nextInt(999999));

        switch (role) {
            case "ARTISAN" -> {
                Artisan a = artisanRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("Artisan", 0L));
                if (a.isVerified()) throw new BadRequestException("Account already verified");
                a.setVerificationCode(code);
                artisanRepository.save(a);
            }
            case "CUSTOMER" -> {
                Customer c = customerRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", 0L));
                if (c.isVerified()) throw new BadRequestException("Account already verified");
                c.setVerificationCode(code);
                customerRepository.save(c);
            }
            default -> throw new BadRequestException("Invalid role for resending code");
        }

        log.info("📧 RESENDING VERIFICATION CODE");
        log.info("To: {}", email);
        log.info("New Code: {}", code);
        sendVerificationEmail(email, code);
    }

    private void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("Kora-Rwanda Admin <nabayoclementine@gmail.com>");
            helper.setTo(toEmail);
            helper.setSubject("Kora-Rwanda - Verify your account");
            
            String htmlContent = String.format(
                "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">" +
                "    <h2 style=\"color: #2e7d32;\">Welcome to Kora-Rwanda!</h2>" +
                "    <p>Your registration is almost complete. Please use the verification code below to activate your account:</p>" +
                "    <div style=\"background-color: #f1f8e9; padding: 15px 25px; display: inline-block; border-radius: 5px; font-size: 24px; font-weight: bold; color: #2e7d32; margin: 20px 0;\">" +
                "        %s" +
                "    </div>" +
                "    <p style=\"color: #555; font-size: 14px;\">This code will expire in 3 minutes.</p>" +
                "    <p style=\"color: #555; font-size: 14px;\">If you did not request this, please ignore this email.</p>" +
                "</div>", code);
                
            helper.setText(htmlContent, true); // true indicates HTML
            mailSender.send(message);
            log.info("Real email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}", toEmail, e);
            // Optionally we could throw an exception to fail registration if email fails
            // throw new RuntimeException("Failed to send verification email.");
        }
    }
}
