package com.company.aiinterview.assessment.service.impl;

import com.company.aiinterview.ai.service.SkillExtractionService;
import com.company.aiinterview.assessment.dto.response.JobDescriptionUploadResponseDto;
import com.company.aiinterview.assessment.service.JobDescriptionParsingService;
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
public class JobDescriptionParsingServiceImpl implements JobDescriptionParsingService {

    private final SkillExtractionService skillExtractionService;
    private final ObjectMapper objectMapper;

    @Override
    public JobDescriptionUploadResponseDto parseAndSave(MultipartFile file) {
        try {
            String rawText = sanitize(new String(file.getBytes(), StandardCharsets.UTF_8));
            String raw = skillExtractionService.extractFromJd(rawText);
            JsonNode node = objectMapper.readTree(extractJson(raw));

            return JobDescriptionUploadResponseDto.builder()
                    .roleApplied(node.path("roleApplied").asText(null))
                    .mandatorySkills(parseArray(node.path("mandatorySkills")))
                    .optionalSkills(parseArray(node.path("optionalSkills")))
                    .experienceRequired(node.path("experienceRequired").asInt(0))
                    .build();
        } catch (Exception e) {
            throw new ExternalServiceException("JD parsing failed: " + e.getMessage());
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
        if (start == -1 || end == -1) throw new ExternalServiceException("No JSON in JD extraction response: " + raw);
        return raw.substring(start, end + 1);
    }
}
