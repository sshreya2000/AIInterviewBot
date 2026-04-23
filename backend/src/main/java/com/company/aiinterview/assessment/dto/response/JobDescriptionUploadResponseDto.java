package com.company.aiinterview.assessment.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobDescriptionUploadResponseDto { Long jobDescriptionId; String roleApplied; java.util.List<String> mandatorySkills; java.util.List<String> optionalSkills; }
