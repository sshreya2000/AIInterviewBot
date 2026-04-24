package com.company.aiinterview.ai.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumeAuditRequestDto {
    String resumeText;
    String parsedJson;
}
