package com.company.aiinterview.assessment.controller;

import com.company.aiinterview.assessment.dto.response.ResumeUploadResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assessment/resume")
public class ResumeController {

    @PostMapping("/upload")
    public ResponseEntity<ResumeUploadResponseDto> upload(@RequestPart("file") MultipartFile file) {
        // TODO: Parse resume and persist metadata.
        return ResponseEntity.ok(new ResumeUploadResponseDto());
    }
}
