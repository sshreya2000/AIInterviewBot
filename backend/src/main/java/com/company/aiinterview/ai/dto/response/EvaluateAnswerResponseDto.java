package com.company.aiinterview.ai.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvaluateAnswerResponseDto { Double score; String feedback; java.util.List<String> strengths; java.util.List<String> improvements; }
