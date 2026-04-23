package com.company.aiinterview.auth.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequestDto { String fullName; String email; String password; String role; }
