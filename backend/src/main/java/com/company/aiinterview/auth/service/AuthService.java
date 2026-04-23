package com.company.aiinterview.auth.service;

public interface AuthService {
    com.company.aiinterview.auth.dto.response.UserResponseDto register(com.company.aiinterview.auth.dto.request.RegisterRequestDto request);
    com.company.aiinterview.auth.dto.response.LoginResponseDto login(com.company.aiinterview.auth.dto.request.LoginRequestDto request);
    com.company.aiinterview.auth.dto.response.UserResponseDto getCurrentUser();
}
