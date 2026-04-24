package com.company.aiinterview.assessment.controller;

import com.company.aiinterview.assessment.dto.response.InterviewResultResponseDto;
import com.company.aiinterview.assessment.service.ResultGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assessment/result")
@RequiredArgsConstructor
public class ResultController {

    private final ResultGenerationService resultGenerationService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<InterviewResultResponseDto> getResult(@PathVariable Long sessionId) {
        return ResponseEntity.ok(resultGenerationService.getResult(sessionId));
    }
}
