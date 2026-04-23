package com.company.aiinterview.ai.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GenerateQuestionRequestDto { java.util.List<String> resumeSkills; java.util.List<String> jdSkills; Integer yearsOfExperience; String category; String difficulty; java.util.List<String> previousQuestions; String answerHistorySummary; }
