package com.company.aiinterview.assessment.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmitTextAnswerRequestDto { Long questionId; String answerText; String languageCode; }
