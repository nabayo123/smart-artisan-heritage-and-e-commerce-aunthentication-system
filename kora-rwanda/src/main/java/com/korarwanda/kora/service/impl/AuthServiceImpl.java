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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ArtisanRepository artisanRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private CooperativeRepository cooperativeRepository;

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
                        .orElseThrow(() -> new ResourceNotFoundException("Artisan", 0L));
                yield new AuthDto.LoginResponse(jwt, a.getEmail(), a.getFullName(), a.getRole(), a.getArtisanId());
            }
            case "CUSTOMER" -> {
                Customer c = customerRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", 0L));
                yield new AuthDto.LoginResponse(jwt, c.getEmail(), c.getFullName(), c.getRole(), c.getCustomerId());
            }
            case "ADMIN" -> {
                Admin adm = adminRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Admin", 0L));
                yield new AuthDto.LoginResponse(jwt, adm.getEmail(), adm.getFullName(), adm.getRole(), adm.getAdminId());
            }
            default -> throw new BadRequestException("Invalid role: " + request.getRole());
        };
    }

    @Override
    public AuthDto.LoginResponse registerArtisan(AuthDto.ArtisanRegisterRequest request) {
        if (artisanRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        Artisan artisan = Artisan.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .districtVillage(request.getDistrictVillage())
                .bio(request.getBio())
                .momoNumber(request.getMomoNumber())
                .role(UserRole.ROLE_ARTISAN)
                .build();

        if (request.getCooperativeId() != null) {
            Cooperative cooperative = cooperativeRepository.findById(request.getCooperativeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cooperative", request.getCooperativeId()));
            artisan.setCooperative(cooperative);
        }

        artisan = artisanRepository.save(artisan);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String jwt = jwtUtils.generateJwtToken(authentication);

        return new AuthDto.LoginResponse(jwt, artisan.getEmail(), artisan.getFullName(),
                artisan.getRole(), artisan.getArtisanId());
    }

    @Override
    public AuthDto.LoginResponse registerCustomer(AuthDto.CustomerRegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(UserRole.ROLE_CUSTOMER)
                .build();

        customer = customerRepository.save(customer);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String jwt = jwtUtils.generateJwtToken(authentication);

        return new AuthDto.LoginResponse(jwt, customer.getEmail(), customer.getFullName(),
                customer.getRole(), customer.getCustomerId());
    }
}
