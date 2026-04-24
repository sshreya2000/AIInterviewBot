package com.company.aiinterview.ai.controller;

import com.company.aiinterview.ai.dto.request.EvaluateAnswerRequestDto;
import com.company.aiinterview.ai.dto.request.ExtractSkillsRequestDto;
import com.company.aiinterview.ai.dto.request.GenerateQuestionRequestDto;
import com.company.aiinterview.ai.dto.response.EvaluateAnswerResponseDto;
import com.company.aiinterview.ai.dto.response.ExtractSkillsResponseDto;
import com.company.aiinterview.ai.dto.response.GenerateQuestionResponseDto;
import com.company.aiinterview.ai.service.GeminiEvaluationService;
import com.company.aiinterview.ai.service.GeminiQuestionService;
import com.company.aiinterview.ai.dto.request.ResumeAuditRequestDto;
import com.company.aiinterview.ai.dto.response.ResumeAuditResponseDto;
import com.company.aiinterview.ai.service.ResumeAuditService;
import com.company.aiinterview.ai.service.SkillExtractionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiQuestionService geminiQuestionService;
    private final GeminiEvaluationService geminiEvaluationService;
    private final SkillExtractionService skillExtractionService;
    private final ResumeAuditService resumeAuditService;
    private final ObjectMapper objectMapper;

    @PostMapping("/question")
    public ResponseEntity<GenerateQuestionResponseDto> question(@RequestBody GenerateQuestionRequestDto request) {
        return ResponseEntity.ok(geminiQuestionService.generateQuestion(request));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateAnswerResponseDto> evaluate(@RequestBody EvaluateAnswerRequestDto request) {
        return ResponseEntity.ok(geminiEvaluationService.evaluate(request));
    }

    @PostMapping("/extract-skills")
    public ResponseEntity<ExtractSkillsResponseDto> extractSkills(@RequestBody ExtractSkillsRequestDto request) {
        try {
            String raw = skillExtractionService.extractFromResume(request.getResumeText());
            int start = raw.indexOf('{'); int end = raw.lastIndexOf('}');
            JsonNode node = objectMapper.readTree(raw.substring(start, end + 1));
            List<String> resumeSkills = new ArrayList<>();
            node.path("skills").forEach(n -> resumeSkills.add(n.asText()));

            String rawJd = skillExtractionService.extractFromJd(request.getJobDescriptionText());
            int s2 = rawJd.indexOf('{'); int e2 = rawJd.lastIndexOf('}');
            JsonNode jdNode = objectMapper.readTree(rawJd.substring(s2, e2 + 1));
            List<String> mandatory = new ArrayList<>();
            List<String> optional = new ArrayList<>();
            jdNode.path("mandatorySkills").forEach(n -> mandatory.add(n.asText()));
            jdNode.path("optionalSkills").forEach(n -> optional.add(n.asText()));

            return ResponseEntity.ok(ExtractSkillsResponseDto.builder()
                    .resumeSkills(resumeSkills)
                    .mandatorySkills(mandatory)
                    .optionalSkills(optional)
                    .build());
        } catch (Exception e) {
            throw new com.company.aiinterview.exception.ExternalServiceException("Skill extraction failed: " + e.getMessage());
        }
    }
}
