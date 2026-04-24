package com.company.aiinterview.assessment.controller;

import com.company.aiinterview.assessment.dto.response.ResumeUploadResponseDto;
import com.company.aiinterview.assessment.service.ResumeParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assessment/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeParsingService resumeParsingService;

    @PostMapping("/upload")
    public ResponseEntity<ResumeUploadResponseDto> upload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(resumeParsingService.parseAndSave(file));
    }
}
