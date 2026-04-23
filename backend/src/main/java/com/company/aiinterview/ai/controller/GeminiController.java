package com.company.aiinterview.ai.controller;

import com.company.aiinterview.ai.dto.request.EvaluateAnswerRequestDto;
import com.company.aiinterview.ai.dto.request.ExtractSkillsRequestDto;
import com.company.aiinterview.ai.dto.request.GenerateQuestionRequestDto;
import com.company.aiinterview.ai.dto.response.EvaluateAnswerResponseDto;
import com.company.aiinterview.ai.dto.response.ExtractSkillsResponseDto;
import com.company.aiinterview.ai.dto.response.GenerateQuestionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class GeminiController {

    @PostMapping("/question")
    public ResponseEntity<GenerateQuestionResponseDto> question(@RequestBody GenerateQuestionRequestDto request) {
        // TODO: Generate dynamic question from Gemini.
        return ResponseEntity.ok(new GenerateQuestionResponseDto());
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateAnswerResponseDto> evaluate(@RequestBody EvaluateAnswerRequestDto request) {
        // TODO: Evaluate answer using Gemini.
        return ResponseEntity.ok(new EvaluateAnswerResponseDto());
    }

    @PostMapping("/extract-skills")
    public ResponseEntity<ExtractSkillsResponseDto> extractSkills(@RequestBody ExtractSkillsRequestDto request) {
        // TODO: Extract resume + JD skills.
        return ResponseEntity.ok(new ExtractSkillsResponseDto());
    }
}
