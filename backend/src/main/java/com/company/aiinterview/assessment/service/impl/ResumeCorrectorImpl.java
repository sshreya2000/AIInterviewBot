package com.company.aiinterview.assessment.service.impl;

import com.company.aiinterview.assessment.service.ParsedResume;
import com.company.aiinterview.assessment.service.ResumeCorrector;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Corrects and improves parsed resume data.
 * - Normalizes skills
 * - Removes duplicates
 * - Infers missing skills
 * - Calculates experience
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeCorrectorImpl implements ResumeCorrector {

    private static final Map<String, String> SKILL_NORMALIZATIONS = Map.ofEntries(
        // Programming Languages
        Map.entry("springboot", "Spring Boot"),
        Map.entry("spring-boot", "Spring Boot"),
        Map.entry("reactjs", "React"),
        Map.entry("react.js", "React"),
        Map.entry("nodejs", "Node.js"),
        Map.entry("node.js", "Node.js"),
        Map.entry("typescript", "TypeScript"),
        Map.entry("javascript", "JavaScript"),
        Map.entry("python", "Python"),
        Map.entry("java", "Java"),
        Map.entry("kotlin", "Kotlin"),
        Map.entry("golang", "Go"),
        Map.entry("rust", "Rust"),
        Map.entry("csharp", "C#"),
        Map.entry("c#", "C#"),
        
        // Databases
        Map.entry("mysql", "MySQL"),
        Map.entry("postgres", "PostgreSQL"),
        Map.entry("mongodb", "MongoDB"),
        Map.entry("redis", "Redis"),
        Map.entry("cassandra", "Cassandra"),
        Map.entry("elasticsearch", "Elasticsearch"),
        
        // Frontend
        Map.entry("html/css", "HTML/CSS"),
        Map.entry("vue", "Vue.js"),
        Map.entry("angular", "Angular"),
        Map.entry("nextjs", "Next.js"),
        Map.entry("webpack", "Webpack"),
        
        // DevOps
        Map.entry("docker", "Docker"),
        Map.entry("kubernetes", "Kubernetes"),
        Map.entry("docker/kubernetes", "Docker, Kubernetes"),
        Map.entry("jenkins", "Jenkins"),
        Map.entry("gitlab", "GitLab"),
        Map.entry("github", "GitHub"),
        Map.entry("aws", "AWS"),
        Map.entry("azure", "Azure"),
        Map.entry("gcp", "Google Cloud"),
        
        // Tools & Frameworks
        Map.entry("apache kafka", "Apache Kafka"),
        Map.entry("rabbitmq", "RabbitMQ"),
        Map.entry("graphql", "GraphQL"),
        Map.entry("rest api", "REST API"),
        Map.entry("microservices", "Microservices"),
        Map.entry("maven", "Maven"),
        Map.entry("gradle", "Gradle"),
        Map.entry("git", "Git"),
        Map.entry("jpa", "JPA"),
        Map.entry("hibernate", "Hibernate"),
        Map.entry("junit", "JUnit"),
        Map.entry("mockito", "Mockito"),
        Map.entry("agile", "Agile"),
        Map.entry("scrum", "Scrum")
    );

    private static final Map<String, Set<String>> SKILL_INFERENCE = Map.ofEntries(
        Map.entry("Spring Boot", Set.of("Java", "Spring", "Maven", "Gradle")),
        Map.entry("React", Set.of("JavaScript", "Node.js", "HTML/CSS", "Webpack")),
        Map.entry("Angular", Set.of("TypeScript", "HTML/CSS", "Node.js")),
        Map.entry("Vue.js", Set.of("JavaScript", "Node.js", "HTML/CSS")),
        Map.entry("Next.js", Set.of("React", "TypeScript", "Node.js")),
        Map.entry("PostgreSQL", Set.of("SQL", "Database Design")),
        Map.entry("MongoDB", Set.of("NoSQL", "Database Design")),
        Map.entry("Docker", Set.of("DevOps", "Container")),
        Map.entry("Kubernetes", Set.of("DevOps", "Orchestration")),
        Map.entry("AWS", Set.of("Cloud Computing")),
        Map.entry("Microservices", Set.of("System Design", "DevOps")),
        Map.entry("GraphQL", Set.of("API Design")),
        Map.entry("REST API", Set.of("API Design"))
    );

    @Override
    public ParsedResume correct(ParsedResume resume) {
        if (resume == null) {
            return ParsedResume.builder()
                    .skills(new ArrayList<>())
                    .inferredSkills(new ArrayList<>())
                    .projects(new ArrayList<>())
                    .yearsOfExperience(0)
                    .education(new ArrayList<>())
                    .build();
        }

        log.debug("Correcting resume for candidate: {}", resume.getCandidateName());

        // 1. Normalize and deduplicate skills
        Set<String> normalizedSkills = normalizeSkills(resume.getSkills());
        log.debug("Normalized skills: {}", normalizedSkills);

        // 2. Infer missing skills
        Set<String> inferredSkills = inferSkills(normalizedSkills);
        log.debug("Inferred skills: {}", inferredSkills);

        // 3. Clean and validate projects
        List<String> cleanedProjects = cleanProjects(resume.getProjects());
        log.debug("Cleaned projects: {}", cleanedProjects);

        // 4. Validate experience
        Integer yearsOfExperience = validateExperience(resume.getYearsOfExperience());
        log.debug("Validated experience: {} years", yearsOfExperience);

        return ParsedResume.builder()
                .candidateName(resume.getCandidateName())
                .skills(new ArrayList<>(normalizedSkills))
                .inferredSkills(new ArrayList<>(inferredSkills))
                .projects(cleanedProjects)
                .yearsOfExperience(yearsOfExperience)
                .education(resume.getEducation())
                .summary(resume.getSummary())
                .build();
    }

    /**
     * Normalizes skill names and removes duplicates
     */
    private Set<String> normalizeSkills(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> normalized = new HashSet<>();
        for (String skill : skills) {
            if (skill == null || skill.trim().isEmpty()) continue;

            String cleaned = skill.trim();
            String lowerCase = cleaned.toLowerCase();

            // Check if it matches any known normalization
            String normalized_skill = SKILL_NORMALIZATIONS.getOrDefault(lowerCase, cleaned);
            if (!normalized_skill.isEmpty()) {
                normalized.add(normalized_skill);
            }
        }

        return normalized;
    }

    /**
     * Infers additional skills based on known skills
     */
    private Set<String> inferSkills(Set<String> skills) {
        Set<String> inferred = new HashSet<>();

        for (String skill : skills) {
            if (SKILL_INFERENCE.containsKey(skill)) {
                inferred.addAll(SKILL_INFERENCE.get(skill));
            }
        }

        // Remove skills that are already in the main skill set
        inferred.removeAll(skills);

        log.debug("Inferred {} additional skills", inferred.size());
        return inferred;
    }

    /**
     * Cleans project names and removes duplicates
     */
    private List<String> cleanProjects(List<String> projects) {
        if (projects == null || projects.isEmpty()) {
            return new ArrayList<>();
        }

        return projects.stream()
                .filter(p -> p != null && !p.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .limit(10) // Limit to 10 projects
                .collect(Collectors.toList());
    }

    /**
     * Validates and corrects experience years
     */
    private Integer validateExperience(Integer years) {
        if (years == null || years < 0) {
            return 0;
        }
        if (years > 70) { // Unrealistic
            return 0;
        }
        return years;
    }

    @Override
    public ParsedResume correctFromJson(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isEmpty()) {
            return ParsedResume.builder()
                    .skills(new ArrayList<>())
                    .inferredSkills(new ArrayList<>())
                    .projects(new ArrayList<>())
                    .yearsOfExperience(0)
                    .education(new ArrayList<>())
                    .build();
        }

        ParsedResume resume = ParsedResume.builder()
                .candidateName(jsonNode.path("candidateName").asText(null))
                .skills(extractStringList(jsonNode, "skills"))
                .inferredSkills(extractStringList(jsonNode, "inferredSkills"))
                .projects(extractStringList(jsonNode, "projects"))
                .yearsOfExperience(jsonNode.path("yearsOfExperience").asInt(0))
                .education(extractStringList(jsonNode, "education"))
                .summary(jsonNode.path("summary").asText(null))
                .build();

        return correct(resume);
    }

    /**
     * Extracts string list from JSON node
     */
    private List<String> extractStringList(JsonNode node, String fieldName) {
        List<String> result = new ArrayList<>();
        JsonNode arrayNode = node.path(fieldName);

        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(item -> {
                String value = item.asText("").trim();
                if (!value.isEmpty()) {
                    result.add(value);
                }
            });
        }

        return result;
    }

    @Override
    public JsonNode correctToJson(ParsedResume resume, ObjectMapper objectMapper) {
        ParsedResume corrected = correct(resume);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("candidateName", corrected.getCandidateName());
        node.putPOJO("skills", corrected.getSkills());
        node.putPOJO("inferredSkills", corrected.getInferredSkills());
        node.putPOJO("projects", corrected.getProjects());
        node.put("yearsOfExperience", corrected.getYearsOfExperience());
        node.putPOJO("education", corrected.getEducation());
        node.put("summary", corrected.getSummary());

        return node;
    }
}

