package com.company.aiinterview.assessment.service.impl;

import com.company.aiinterview.ai.service.SkillExtractionService;
import com.company.aiinterview.assessment.dto.response.ResumeUploadResponseDto;
import com.company.aiinterview.assessment.service.ResumeParsingService;
import com.company.aiinterview.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeParsingServiceImpl implements ResumeParsingService {

    private final SkillExtractionService skillExtractionService;
    private final ObjectMapper objectMapper;

    @Override
    public ResumeUploadResponseDto parseAndSave(MultipartFile file) {
        try {
            String rawText = sanitize(new String(file.getBytes(), StandardCharsets.UTF_8));
            String raw = skillExtractionService.extractFromResume(rawText);
            JsonNode node = objectMapper.readTree(extractJson(raw));

            return ResumeUploadResponseDto.builder()
                    .extractedCandidateName(node.path("candidateName").asText(null))
                    .extractedSkills(parseArray(node.path("skills")))
                    .inferredSkills(parseArray(node.path("inferredSkills")))
                    .derivedYearsOfExperience(node.path("yearsOfExperience").asInt(0))
                    .education(parseArray(node.path("education")))
                    .build();
        } catch (Exception e) {
            throw new ExternalServiceException("Resume parsing failed: " + e.getMessage());
        }
    }

    private List<String> parseArray(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(n -> {
                String val = n.asText("").trim();
                if (!val.isEmpty()) result.add(val);
            });
        }
        return result;
    }

    private String sanitize(String text) {
        return text.replace("\u0000", "").replaceAll("[^\\x09\\x0A\\x0D\\x20-\\uD7FF\\uE000-\\uFFFD]", "");
    }

    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start == -1 || end == -1) throw new ExternalServiceException("No JSON in resume extraction response: " + raw);
        return raw.substring(start, end + 1);
    }
}
