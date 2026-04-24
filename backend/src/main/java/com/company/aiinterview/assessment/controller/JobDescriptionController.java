package com.company.aiinterview.assessment.controller;

import com.company.aiinterview.assessment.dto.response.JobDescriptionUploadResponseDto;
import com.company.aiinterview.assessment.service.JobDescriptionParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assessment/jd")
@RequiredArgsConstructor
public class JobDescriptionController {

    private final JobDescriptionParsingService jobDescriptionParsingService;

    @PostMapping("/upload")
    public ResponseEntity<JobDescriptionUploadResponseDto> upload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(jobDescriptionParsingService.parseAndSave(file));
    }
}
