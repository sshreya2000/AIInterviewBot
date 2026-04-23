package com.company.aiinterview.auth.controller;

import com.company.aiinterview.auth.dto.request.LoginRequestDto;
import com.company.aiinterview.auth.dto.request.RegisterRequestDto;
import com.company.aiinterview.auth.dto.response.LoginResponseDto;
import com.company.aiinterview.auth.dto.response.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegisterRequestDto request) {
        // TODO: Delegate to AuthService.register
        return ResponseEntity.ok(new UserResponseDto());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        // TODO: Delegate to AuthService.login
        return ResponseEntity.ok(new LoginResponseDto());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me() {
        // TODO: Resolve authenticated user via AuthService.getCurrentUser
        return ResponseEntity.ok(new UserResponseDto());
    }
}
