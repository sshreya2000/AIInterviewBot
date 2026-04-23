package com.company.aiinterview.assessment.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumeUploadResponseDto { Long resumeId; String fileName; String extractedCandidateName; Integer derivedYearsOfExperience; java.util.List<String> extractedSkills; }
