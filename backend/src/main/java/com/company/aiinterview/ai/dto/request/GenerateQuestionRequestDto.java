package com.company.aiinterview.ai.dto.request;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GenerateQuestionRequestDto {
    List<String> resumeSkills;
    List<String> inferredSkills;
    List<String> jdSkills;
    Integer yearsOfExperience;
    String category;
    String difficulty;
    List<String> previousQuestions;
    String answerHistorySummary;
}
