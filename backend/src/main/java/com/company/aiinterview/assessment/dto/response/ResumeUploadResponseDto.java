package com.company.aiinterview.assessment.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumeUploadResponseDto {
    String extractedCandidateName;
    List<String> extractedSkills;
    List<String> inferredSkills;
    Integer derivedYearsOfExperience;
    List<String> education;
}
