package com.company.aiinterview.assessment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interface for correcting and improving parsed resume data
 */
public interface ResumeCorrector {
    
    /**
     * Corrects a parsed resume object
     * - Normalizes skills
     * - Removes duplicates
     * - Infers missing skills
     * - Validates experience
     */
    ParsedResume correct(ParsedResume resume);
    
    /**
     * Corrects a resume from JSON node
     */
    ParsedResume correctFromJson(JsonNode jsonNode);
    
    /**
     * Converts corrected resume back to JSON
     */
    JsonNode correctToJson(ParsedResume resume, ObjectMapper objectMapper);
}

