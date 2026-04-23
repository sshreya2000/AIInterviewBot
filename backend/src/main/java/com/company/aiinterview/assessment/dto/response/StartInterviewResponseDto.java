package com.company.aiinterview.assessment.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StartInterviewResponseDto { Long sessionId; String status; String interviewMode; String firstQuestionCategory; }
