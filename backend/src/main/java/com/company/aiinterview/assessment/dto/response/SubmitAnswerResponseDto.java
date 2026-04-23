package com.company.aiinterview.assessment.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmitAnswerResponseDto { Long sessionId; Long questionId; Double answerScore; String feedbackSummary; Boolean followUpGenerated; }
