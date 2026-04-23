package com.company.aiinterview.ai.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeminiGenerateResponseDto { String rawResponse; String model; java.time.Instant generatedAt; }
