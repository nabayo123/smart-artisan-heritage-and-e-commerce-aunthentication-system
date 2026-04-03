package com.korarwanda.kora.service;

import com.korarwanda.kora.dto.AuthDto;

public interface AuthService {
    AuthDto.LoginResponse login(AuthDto.LoginRequest request);
    AuthDto.LoginResponse registerArtisan(AuthDto.ArtisanRegisterRequest request);
    AuthDto.LoginResponse registerCustomer(AuthDto.CustomerRegisterRequest request);
}
