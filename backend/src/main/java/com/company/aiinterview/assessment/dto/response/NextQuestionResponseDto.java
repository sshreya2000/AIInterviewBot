package com.company.aiinterview.assessment.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NextQuestionResponseDto { Long questionId; String category; String difficulty; String questionText; Integer sequenceNo; }
