package com.company.aiinterview.assessment.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobDescriptionUploadResponseDto {
    String roleApplied;
    List<String> mandatorySkills;
    List<String> optionalSkills;
    Integer experienceRequired;
}
