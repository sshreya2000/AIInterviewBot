package com.company.aiinterview.auth.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequestDto { String email; String password; }
