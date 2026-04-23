package com.company.aiinterview.auth.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponseDto { Long id; String fullName; String email; String role; }
