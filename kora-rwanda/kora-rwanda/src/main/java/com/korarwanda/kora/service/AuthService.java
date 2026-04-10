package com.korarwanda.kora.service;

import com.korarwanda.kora.dto.AuthDto;

public interface AuthService {
    AuthDto.LoginResponse login(AuthDto.LoginRequest request);
    AuthDto.LoginResponse registerArtisan(AuthDto.ArtisanRegisterRequest request);
    AuthDto.LoginResponse registerCustomer(AuthDto.CustomerRegisterRequest request);
    AuthDto.LoginResponse registerAdmin(AuthDto.AdminRegisterRequest request);
    void updatePassword(AuthDto.PasswordUpdateRequest request);
    void verifyAccount(AuthDto.VerifyAccountRequest request);
    void resendVerificationCode(AuthDto.ResendVerificationRequest request);
}
