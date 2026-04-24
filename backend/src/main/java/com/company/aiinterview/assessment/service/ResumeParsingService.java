package com.company.aiinterview.assessment.service;

import com.company.aiinterview.assessment.dto.response.ResumeUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeParsingService {
    ResumeUploadResponseDto parseAndSave(MultipartFile file);
}
