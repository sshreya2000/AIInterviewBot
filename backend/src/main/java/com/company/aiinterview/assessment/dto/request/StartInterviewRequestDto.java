package com.company.aiinterview.assessment.dto.request;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StartInterviewRequestDto {
    String candidateName;
    Integer yearsOfExperience;
    List<String> resumeSkills;
    List<String> inferredSkills;
    String roleApplied;
    List<String> jdSkills;
    String interviewMode;
    Integer maxQuestions;
}
