package com.company.aiinterview.ai.service;

public final class PromptTemplateSamples {
    private PromptTemplateSamples() {}

    public static final String RESUME_EXTRACTION_TEMPLATE = """
        You are an expert resume parser.

        Extract ONLY the following from the resume below. Be strict — no soft skills, no extra fields.

        Rules:
        - Normalize skill names (e.g. "springboot" → "Spring Boot", "reactjs" → "React")
        - Remove duplicate skills
        - Infer missing skills from context (e.g. Spring Boot → Java, React → JavaScript)
        - Calculate total years of experience from dates if present
        - If no experience → yearsOfExperience = 0

        Resume:
        {{resumeText}}

        Return ONLY this JSON, no explanation, no markdown:
        {
          "candidateName": "string",
          "skills": ["normalized tech skills only"],
          "inferredSkills": ["skills inferred from context"],
          "yearsOfExperience": 0,
          "education": ["degree - institution - year"]
        }
        """;

    public static final String JD_EXTRACTION_TEMPLATE = """
        You are an expert job description analyzer.

        Extract ONLY the following from the job description below. Be strict — no soft skills, no extra fields.

        Rules:
        - Normalize skill names (e.g. "nodejs" → "Node.js", "reactjs" → "React")
        - Remove duplicates
        - Infer missing skills (e.g. Spring Boot → Java)
        - Separate mandatory vs optional clearly

        Job Description:
        {{jobDescriptionText}}

        Return ONLY this JSON, no explanation, no markdown:
        {
          "roleApplied": "string",
          "mandatorySkills": ["required tech skills only"],
          "optionalSkills": ["nice-to-have tech skills only"],
          "experienceRequired": 0
        }
        """;

    public static final String QUESTION_GENERATION_TEMPLATE = """
        You are a strict technical interviewer.

        You MUST generate questions ONLY based on the tech stacks provided below.
        Do NOT ask generic questions. Every question must be directly tied to one or more of the listed skills.

        Candidate Profile:
        - Years of experience: {{yearsOfExperience}}
        - Resume tech stack: {{resumeSkills}}
        - Inferred skills (from resume context): {{inferredSkills}}
        - Job Description required tech stack: {{jdSkills}}

        Interview Context:
        - Category: {{category}}
        - Difficulty: {{difficulty}}
        - Already asked questions (DO NOT repeat): {{previousQuestions}}
        - Recent answer performance: {{answerHistorySummary}}

        Rules:
        - Pick a skill from the tech stacks above and ask a specific, practical question about it
        - For TECHNICAL category: ask concept or architecture questions on the listed skills
        - For CODING category: ask to write or explain code using the listed skills
        - For PROBLEM_SOLVING category: ask a scenario-based problem using the listed skills
        - For RESUME_RELEVANCE category: ask about their actual experience with the listed skills
        - For COMMUNICATION category: ask how they would explain a concept from the listed skills to a non-technical person
        - Match difficulty to experience: EASY for <2 yrs, MEDIUM for 2-5 yrs, HARD for 5+ yrs
        - Never repeat a question from the already asked list

        Return ONLY this JSON, no explanation, no markdown:
        {
          "questionText": "string",
          "category": "string",
          "difficulty": "string",
          "focusSkills": ["the specific skills this question targets"]
        }
        """;

    public static final String EVALUATION_TEMPLATE = """
        You are an expert technical interviewer evaluating a candidate's answer.

        Question: {{questionText}}
        Category: {{category}}
        Difficulty: {{difficulty}}
        Candidate Experience: {{yearsOfExperience}} years
        Candidate Answer: {{answerText}}

        Return ONLY this JSON, no explanation, no markdown:
        {
          "score": 0.0,
          "feedback": "string",
          "strengths": ["string"],
          "improvements": ["string"]
        }
        Score must be between 0.0 and 10.0.
        """;

    public static final String RESUME_AUDIT_TEMPLATE = """
        You are a senior AI architect reviewing a resume parsing system output.

        Resume:
        {{resume_text}}

        System Output:
        {{json_output}}

        Return ONLY this JSON, no explanation, no markdown:
        {
          "score": 0,
          "is_production_ready": false,
          "issues": ["string"],
          "missing_features": ["string"],
          "improvements": ["string"]
        }
        """;

    public static final String FINAL_SUMMARY_TEMPLATE = """
        You are an expert technical interview evaluator.

        Role Applied: {{roleApplied}}
        Candidate Experience: {{yearsOfExperience}} years

        Interview Q&A:
        {{interviewTranscript}}

        Category Scores:
        {{categoryScores}}

        Return ONLY this JSON, no explanation, no markdown:
        {
          "overallScore": 0.0,
          "recommendation": "HIRE",
          "strengths": ["string"],
          "weaknesses": ["string"],
          "summary": "string"
        }
        recommendation must be one of: HIRE, HOLD, REJECT.
        overallScore must be between 0.0 and 10.0.
        """;
}
