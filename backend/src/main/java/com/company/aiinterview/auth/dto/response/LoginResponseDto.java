package com.company.aiinterview.auth.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponseDto { String accessToken; String tokenType; Long expiresInSeconds; UserResponseDto user; }
