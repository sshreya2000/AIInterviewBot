package com.company.aiinterview.assessment.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StartInterviewRequestDto { Long candidateProfileId; Long jobDescriptionId; String interviewMode; String preferredLanguage; Integer maxQuestions; }
