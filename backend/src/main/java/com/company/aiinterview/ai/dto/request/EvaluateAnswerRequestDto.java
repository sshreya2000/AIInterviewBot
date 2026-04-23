package com.company.aiinterview.ai.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvaluateAnswerRequestDto { String question; String answer; String category; String difficulty; Integer yearsOfExperience; }
