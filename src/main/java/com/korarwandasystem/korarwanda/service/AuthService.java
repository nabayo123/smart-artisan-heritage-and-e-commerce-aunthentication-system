package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.dto.LoginRequest;
import com.korarwandasystem.korarwanda.dto.RegisterRequest;
import com.korarwandasystem.korarwanda.dto.AuthResponse;
import com.korarwandasystem.korarwanda.model.User;
import com.korarwandasystem.korarwanda.model.UserType;
import com.korarwandasystem.korarwanda.security.JwtTokenProvider;
import com.korarwandasystem.korarwanda.security.UserPrincipal;
import com.korarwandasystem.korarwanda.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // Constructor Injection is safer and resolves autowiring symbol errors
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
                jwt,
                user.getUserId(), // Changed from getId()
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType().toString()
        );
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setUserType(UserType.valueOf(registerRequest.getUserType().toUpperCase()));
        user.setBusinessName(registerRequest.getBusinessName());
        user.setDescription(registerRequest.getDescription());
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(
                jwt,
                savedUser.getUserId(), // Changed from getId()
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getUserType().toString()
        );
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userRepository.findById(userPrincipal.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("User not authenticated");
    }

    public boolean isCurrentUserAdmin() {
        return getCurrentUser().getUserType() == UserType.ADMIN;
    }

    public boolean isCurrentUserArtisan() {
        return getCurrentUser().getUserType() == UserType.ARTISAN;
    }

    public boolean isCurrentUserCustomer() {
        return getCurrentUser().getUserType() == UserType.CUSTOMER;
    }

    public boolean canAccessUserResource(Long targetUserId) {
        User currentUser = getCurrentUser();
        return currentUser.getUserId().equals(targetUserId) || currentUser.getUserType() == UserType.ADMIN;
    }

    public String refreshToken(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromJWT(token);
            return jwtTokenProvider.generateTokenFromEmail(email);
        }
        throw new RuntimeException("Invalid token");
    }

    public void logout(String token) {
        jwtTokenProvider.blacklistToken(token);
        SecurityContextHolder.clearContext();
    }
}