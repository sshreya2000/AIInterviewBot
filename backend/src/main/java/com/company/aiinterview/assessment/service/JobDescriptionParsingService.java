package com.company.aiinterview.assessment.service;

import com.company.aiinterview.assessment.dto.response.JobDescriptionUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface JobDescriptionParsingService {
    JobDescriptionUploadResponseDto parseAndSave(MultipartFile file);
}
