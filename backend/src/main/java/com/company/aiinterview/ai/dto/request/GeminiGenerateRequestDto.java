package com.company.aiinterview.ai.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeminiGenerateRequestDto { String model; String prompt; Double temperature; Integer maxOutputTokens; }
