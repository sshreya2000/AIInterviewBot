package com.company.aiinterview.assessment.controller;

import com.company.aiinterview.assessment.dto.request.StartInterviewRequestDto;
import com.company.aiinterview.assessment.dto.request.SubmitTextAnswerRequestDto;
import com.company.aiinterview.assessment.dto.response.NextQuestionResponseDto;
import com.company.aiinterview.assessment.dto.response.StartInterviewResponseDto;
import com.company.aiinterview.assessment.dto.response.SubmitAnswerResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assessment/interview")
public class InterviewController {

    @PostMapping("/start")
    public ResponseEntity<StartInterviewResponseDto> start(@RequestBody StartInterviewRequestDto request) {
        // TODO: Create interview session and first question.
        return ResponseEntity.ok(new StartInterviewResponseDto());
    }

    @GetMapping("/{sessionId}/next-question")
    public ResponseEntity<NextQuestionResponseDto> nextQuestion(@PathVariable Long sessionId) {
        // TODO: Fetch/generate next question.
        return ResponseEntity.ok(new NextQuestionResponseDto());
    }

    @PostMapping("/{sessionId}/answer/text")
    public ResponseEntity<SubmitAnswerResponseDto> submitTextAnswer(@PathVariable Long sessionId,
                                                                     @RequestBody SubmitTextAnswerRequestDto request) {
        // TODO: Evaluate and persist answer.
        return ResponseEntity.ok(new SubmitAnswerResponseDto());
    }

    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long sessionId) {
        // TODO: Mark interview complete and trigger result generation.
        return ResponseEntity.ok().build();
    }
}
