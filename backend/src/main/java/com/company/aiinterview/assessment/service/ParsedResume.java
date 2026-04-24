package com.company.aiinterview.assessment.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;

/**
 * Resume parser data model.
 * Extracts and corrects resume information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParsedResume {
    private String candidateName;
    private List<String> skills;
    private List<String> inferredSkills;
    private List<String> projects;
    private Integer yearsOfExperience;
    private List<String> education;
    private String summary;
    
    /**
     * Merges skills and inferred skills into single list
     */
    public List<String> getAllSkills() {
        List<String> all = new java.util.ArrayList<>();
        if (skills != null) all.addAll(skills);
        if (inferredSkills != null) all.addAll(inferredSkills);
        // Remove duplicates
        return new java.util.ArrayList<>(new java.util.HashSet<>(all));
    }
}

