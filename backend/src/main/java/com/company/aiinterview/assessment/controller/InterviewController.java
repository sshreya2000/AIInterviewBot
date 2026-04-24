package com.company.aiinterview.assessment.controller;

import com.company.aiinterview.assessment.dto.request.StartInterviewRequestDto;
import com.company.aiinterview.assessment.dto.request.SubmitTextAnswerRequestDto;
import com.company.aiinterview.assessment.dto.response.NextQuestionResponseDto;
import com.company.aiinterview.assessment.dto.response.StartInterviewResponseDto;
import com.company.aiinterview.assessment.dto.response.SubmitAnswerResponseDto;
import com.company.aiinterview.assessment.service.InterviewSessionService;
import com.company.aiinterview.assessment.service.QuestionOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assessment/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewSessionService interviewSessionService;
    private final QuestionOrchestrationService questionOrchestrationService;

    @PostMapping("/start")
    public ResponseEntity<StartInterviewResponseDto> start(@RequestBody StartInterviewRequestDto request) {
        return ResponseEntity.ok(interviewSessionService.startSession(request));
    }

    @GetMapping("/{sessionId}/next-question")
    public ResponseEntity<NextQuestionResponseDto> nextQuestion(@PathVariable Long sessionId) {
        return ResponseEntity.ok(questionOrchestrationService.nextQuestion(sessionId));
    }

    @PostMapping("/{sessionId}/answer/text")
    public ResponseEntity<SubmitAnswerResponseDto> submitTextAnswer(@PathVariable Long sessionId,
                                                                     @RequestBody SubmitTextAnswerRequestDto request) {
        return ResponseEntity.ok(interviewSessionService.submitTextAnswer(sessionId, request));
    }

    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long sessionId) {
        interviewSessionService.completeSession(sessionId);
        return ResponseEntity.ok().build();
    }
}
