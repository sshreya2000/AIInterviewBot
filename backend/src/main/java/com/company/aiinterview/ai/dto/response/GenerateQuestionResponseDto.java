package com.company.aiinterview.ai.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GenerateQuestionResponseDto { String questionText; String category; String difficulty; java.util.List<String> focusSkills; }
