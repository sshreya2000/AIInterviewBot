package com.company.aiinterview.ai.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExtractSkillsResponseDto { java.util.List<String> mandatorySkills; java.util.List<String> optionalSkills; java.util.List<String> resumeSkills; }
