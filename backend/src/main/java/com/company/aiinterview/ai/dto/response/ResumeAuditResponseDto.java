package com.company.aiinterview.ai.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumeAuditResponseDto {
    Integer score;
    Boolean isProductionReady;
    List<String> issues;
    List<String> missingFeatures;
    List<String> improvements;
}
