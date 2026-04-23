package com.company.aiinterview.assessment.controller;

import com.company.aiinterview.assessment.dto.response.InterviewResultResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assessment/result")
public class ResultController {

    @GetMapping("/{sessionId}")
    public ResponseEntity<InterviewResultResponseDto> getResult(@PathVariable Long sessionId) {
        // TODO: Return category-wise result report.
        return ResponseEntity.ok(new InterviewResultResponseDto());
    }
}
