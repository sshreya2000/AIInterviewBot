package com.company.aiinterview.assessment.controller;

import com.company.aiinterview.assessment.dto.response.JobDescriptionUploadResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assessment/jd")
public class JobDescriptionController {

    @PostMapping("/upload")
    public ResponseEntity<JobDescriptionUploadResponseDto> upload(@RequestPart("file") MultipartFile file) {
        // TODO: Parse JD and persist metadata.
        return ResponseEntity.ok(new JobDescriptionUploadResponseDto());
    }
}
