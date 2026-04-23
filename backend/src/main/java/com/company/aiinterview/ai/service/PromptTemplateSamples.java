package com.company.aiinterview.ai.service;

public final class PromptTemplateSamples {
    private PromptTemplateSamples() {}

    public static final String QUESTION_GENERATION_TEMPLATE = """
        You are an expert technical interviewer.
        Candidate years of experience: {{yearsOfExperience}}
        Resume skills: {{resumeSkills}}
        JD skills: {{jdSkills}}
        Category: {{category}}
        Difficulty: {{difficulty}}
        Previous questions: {{previousQuestions}}
        Answer history summary: {{answerHistorySummary}}

        Generate one non-repeated interview question grounded in resume + JD context.
        Return JSON with fields: questionText, category, difficulty, focusSkills.
        """;
}
