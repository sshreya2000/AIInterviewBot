package com.company.aiinterview.ai.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExtractSkillsRequestDto { String resumeText; String jobDescriptionText; }
