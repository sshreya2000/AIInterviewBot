# Phase 1 — AI Integration Documentation (Text Mode Only)

> **Scope:** This document covers the complete backend AI integration plan for Phase 1 of the AI Interview Bot.
> Phase 1 considers **text input only** — resume as plain text/PDF, answers typed by the candidate.
> Voice and translation are out of scope for this phase.

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Full Flow Diagram](#2-full-flow-diagram)
3. [Gemini API Integration](#3-gemini-api-integration)
4. [Step-by-Step Flow](#4-step-by-step-flow)
5. [Gemini Prompt Templates](#5-gemini-prompt-templates)
6. [Category & Difficulty Strategy](#6-category--difficulty-strategy)
7. [Rating & Scoring Model](#7-rating--scoring-model)
8. [API Endpoints Reference](#8-api-endpoints-reference)
9. [Database Entities Involved](#9-database-entities-involved)
10. [Implementation Order](#10-implementation-order)
11. [Configuration](#11-configuration)

---

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (React)                      │
└────────────────────────────┬────────────────────────────────┘
                             │ REST
┌────────────────────────────▼────────────────────────────────┐
│                   Spring Boot Backend                        │
│                                                              │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │  assessment │  │      ai      │  │      config      │   │
│  │  module     │  │    module    │  │  GeminiConfig    │   │
│  │             │  │              │  │  RestClientConfig│   │
│  │ Controllers │  │ GeminiClient │  └──────────────────┘   │
│  │ Services    │◄─┤ Services     │                          │
│  │ Repos       │  │ PromptBuilder│                          │
│  └──────┬──────┘  └──────┬───────┘                         │
│         │                │                                  │
│         └────────────────┘                                  │
│                   │                                          │
│            PostgreSQL DB                                     │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTPS REST
┌────────────────────────────▼────────────────────────────────┐
│         Gemini API (generativelanguage.googleapis.com)       │
│         Model: gemini-1.5-flash                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Full Flow Diagram

```
Candidate
   │
   ├─► POST /api/v1/assessment/resume/upload
   │         │
   │         ├─ Extract raw text from file
   │         ├─ Call Gemini: resume skill extraction prompt
   │         ├─ Save ResumeEntity
   │         ├─ Save CandidateProfileEntity
   │         └─ Return: resumeId, candidateProfileId
   │
   ├─► POST /api/v1/assessment/jd/upload
   │         │
   │         ├─ Extract raw text from file
   │         ├─ Call Gemini: JD skill extraction prompt
   │         ├─ Save JobDescriptionEntity
   │         └─ Return: jobDescriptionId
   │
   ├─► POST /api/v1/assessment/interview/start
   │         │
   │         ├─ Load CandidateProfile + JobDescription
   │         ├─ Create InterviewSessionEntity (IN_PROGRESS)
   │         ├─ Generate first question via Gemini
   │         ├─ Save InterviewQuestionEntity (seq=1)
   │         └─ Return: sessionId, firstQuestion
   │
   ├─► [LOOP until maxQuestions reached]
   │   │
   │   ├─► GET /api/v1/assessment/interview/{sessionId}/next-question
   │   │         │
   │   │         ├─ Determine category + difficulty
   │   │         ├─ Build prompt with history context
   │   │         ├─ Call Gemini: question generation prompt
   │   │         ├─ Save InterviewQuestionEntity
   │   │         └─ Return: questionId, questionText, category, difficulty
   │   │
   │   └─► POST /api/v1/assessment/interview/{sessionId}/answer/text
   │             │
   │             ├─ Load question context
   │             ├─ Call Gemini: evaluation prompt
   │             ├─ Save InterviewAnswerEntity (with score)
   │             └─ Return: answerScore, feedbackSummary
   │
   ├─► POST /api/v1/assessment/interview/{sessionId}/complete
   │         │
   │         ├─ Mark session COMPLETED
   │         └─ Trigger result generation
   │
   └─► GET /api/v1/assessment/result/{sessionId}
             │
             ├─ Aggregate scores per category
             ├─ Save CategoryScoreEntity (per category)
             ├─ Call Gemini: final summary prompt
             ├─ Save InterviewResultEntity
             └─ Return: InterviewResultResponseDto (all scores + recommendation)
```

---

## 3. Gemini API Integration

### Base URL
```
https://generativelanguage.googleapis.com
```

### Endpoint used
```
POST /v1beta/models/{model}:generateContent?key={apiKey}
```

### Request format sent to Gemini
```json
{
  "contents": [
    {
      "parts": [
        {
          "text": "<your prompt here>"
        }
      ]
    }
  ],
  "generationConfig": {
    "temperature": 0.7,
    "maxOutputTokens": 1024
  }
}
```

### Response format received from Gemini
```json
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "<generated text or JSON string>"
          }
        ]
      }
    }
  ]
}
```

### How the backend extracts the response
```
response → candidates[0] → content → parts[0] → text
```
This raw text is then parsed as JSON using Jackson `ObjectMapper` to extract structured fields.

### Classes involved

| Class | Role |
|---|---|
| `GeminiConfig` | Binds `gemini.*` yml properties into a `@ConfigurationProperties` bean |
| `RestClientConfig` | Declares a `RestClient` bean configured with Gemini base URL |
| `GeminiClient` | Makes the actual HTTP POST call and extracts `candidates[0].content.parts[0].text` |
| `GeminiGenerateRequestDto` | Wraps model, prompt, temperature, maxOutputTokens |
| `GeminiGenerateResponseDto` | Holds rawResponse, model, generatedAt |

---

## 4. Step-by-Step Flow

### Step 1 — Resume Upload & Parsing

**Endpoint:** `POST /api/v1/assessment/resume/upload`
**Controller:** `ResumeController`
**Service:** `ResumeParsingService` → `ResumeParsingServiceImpl`
**AI Service:** `SkillExtractionService` → `SkillExtractionServiceImpl`

**What happens:**
1. Frontend sends resume file as `multipart/form-data`
2. Phase 1: read file bytes as UTF-8 string (plain text file)
3. Call `SkillExtractionService.extractFromResume(rawText)`
4. `SkillExtractionServiceImpl` builds the resume skill extraction prompt via `PromptBuilderService`
5. Calls `GeminiClient.generateContent(...)`
6. Parses Gemini JSON response:
   ```json
   {
     "candidateName": "John Doe",
     "skills": ["Java", "Spring Boot", "PostgreSQL"],
     "yearsOfExperience": 4,
     "projectKeywords": ["microservices", "REST API"],
     "companyHistory": "TCS, Infosys"
   }
   ```
7. Saves `ResumeEntity` (fileName, rawText, uploadedAt)
8. Saves `CandidateProfileEntity` (candidateName, yearsOfExperience, skillsCsv, projectKeywordsCsv, companyHistory)
9. Returns `ResumeUploadResponseDto` containing `resumeId` and `candidateProfileId`

---

### Step 2 — JD Upload & Parsing

**Endpoint:** `POST /api/v1/assessment/jd/upload`
**Controller:** `JobDescriptionController`
**Service:** `JobDescriptionParsingService` → `JobDescriptionParsingServiceImpl`
**AI Service:** `SkillExtractionService` → `SkillExtractionServiceImpl`

**What happens:**
1. Frontend sends JD file as `multipart/form-data`
2. Read file bytes as UTF-8 string
3. Call `SkillExtractionService.extractFromJd(rawText)`
4. Builds JD skill extraction prompt via `PromptBuilderService`
5. Calls `GeminiClient.generateContent(...)`
6. Parses Gemini JSON response:
   ```json
   {
     "roleApplied": "Backend Engineer",
     "mandatorySkills": ["Java", "Spring Boot", "Microservices"],
     "optionalSkills": ["Kubernetes", "AWS"]
   }
   ```
7. Saves `JobDescriptionEntity` (fileName, rawText, roleApplied, uploadedAt)
8. Returns `JobDescriptionUploadResponseDto` containing `jobDescriptionId`

---

### Step 3 — Start Interview Session

**Endpoint:** `POST /api/v1/assessment/interview/start`
**Controller:** `InterviewController`
**Service:** `InterviewSessionService` → `InterviewSessionServiceImpl`

**Request body:**
```json
{
  "candidateProfileId": 1,
  "jobDescriptionId": 1,
  "interviewMode": "TEXT",
  "preferredLanguage": "en",
  "maxQuestions": 10
}
```

**What happens:**
1. Load `CandidateProfileEntity` by `candidateProfileId`
2. Load `JobDescriptionEntity` by `jobDescriptionId`
3. Create and save `InterviewSessionEntity` (status=`IN_PROGRESS`, mode=`TEXT`, startedAt=now)
4. Determine first category from the rotation (see Section 6)
5. Generate first question immediately via `QuestionOrchestrationService`
6. Save `InterviewQuestionEntity` (sequenceNo=1)
7. Return `StartInterviewResponseDto`:
   ```json
   {
     "sessionId": 101,
     "status": "IN_PROGRESS",
     "interviewMode": "TEXT",
     "firstQuestionCategory": "TECHNICAL"
   }
   ```

---

### Step 4 — Dynamic Question Generation

**Endpoint:** `GET /api/v1/assessment/interview/{sessionId}/next-question`
**Controller:** `InterviewController`
**Service:** `QuestionOrchestrationService` → `QuestionOrchestrationServiceImpl`
**AI Service:** `GeminiQuestionService` → `GeminiQuestionServiceImpl`

**What happens:**
1. Load `InterviewSessionEntity` by sessionId
2. Load `CandidateProfileEntity` and `JobDescriptionEntity` linked to session
3. Count existing questions → determine `sequenceNo`
4. Determine current **category** based on sequence (see Section 6)
5. Determine current **difficulty** based on recent answer scores (see Section 6)
6. Load all previous question texts for this session (to avoid repetition)
7. Load answer history summary (last 3 scores + feedback snippets)
8. Call `GeminiQuestionService.generateQuestion(GenerateQuestionRequestDto)`
9. `GeminiQuestionServiceImpl`:
   - Calls `PromptBuilderService.buildQuestionPrompt(...)` to fill all `{{placeholders}}`
   - Calls `GeminiClient.generateContent(...)`
   - Parses JSON response:
     ```json
     {
       "questionText": "How does Spring Security handle JWT authentication?",
       "category": "TECHNICAL",
       "difficulty": "MEDIUM",
       "focusSkills": ["Spring Security", "JWT"]
     }
     ```
10. Save `InterviewQuestionEntity`
11. Return `NextQuestionResponseDto`:
    ```json
    {
      "questionId": 5,
      "category": "TECHNICAL",
      "difficulty": "MEDIUM",
      "questionText": "How does Spring Security handle JWT authentication?",
      "sequenceNo": 5
    }
    ```

---

### Step 5 — Submit Text Answer & Evaluate

**Endpoint:** `POST /api/v1/assessment/interview/{sessionId}/answer/text`
**Controller:** `InterviewController`
**Service:** `InterviewSessionService` → `InterviewSessionServiceImpl`
**AI Service:** `GeminiEvaluationService` → `GeminiEvaluationServiceImpl`

**Request body:**
```json
{
  "questionId": 5,
  "answerText": "Spring Security validates the JWT token in a filter before the request reaches the controller...",
  "languageCode": "en"
}
```

**What happens:**
1. Load `InterviewQuestionEntity` by `questionId` (to get question text, category, difficulty)
2. Call `GeminiEvaluationService.evaluate(EvaluateAnswerRequestDto)`
3. `GeminiEvaluationServiceImpl`:
   - Calls `PromptBuilderService.buildEvaluationPrompt(...)`
   - Calls `GeminiClient.generateContent(...)`
   - Parses JSON response:
     ```json
     {
       "score": 7.5,
       "feedback": "Good understanding of JWT filter chain. Missed mentioning token expiry handling.",
       "strengths": ["Correct filter chain explanation", "Mentioned SecurityContext"],
       "improvements": ["Add token expiry handling", "Mention refresh token flow"]
     }
     ```
4. Save `InterviewAnswerEntity` (sessionId, questionId, answerText, languageCode, score=7.5)
5. Return `SubmitAnswerResponseDto`:
   ```json
   {
     "sessionId": 101,
     "questionId": 5,
     "answerScore": 7.5,
     "feedbackSummary": "Good understanding of JWT filter chain. Missed mentioning token expiry handling.",
     "followUpGenerated": false
   }
   ```

---

### Step 6 — Complete Interview

**Endpoint:** `POST /api/v1/assessment/interview/{sessionId}/complete`
**Controller:** `InterviewController`
**Service:** `InterviewSessionService`

**What happens:**
1. Load `InterviewSessionEntity`
2. Set status=`COMPLETED`, completedAt=now
3. Save entity
4. Trigger `ResultGenerationService.generate(sessionId)` synchronously
5. Return HTTP 200

---

### Step 7 — Result Generation & Rating

**Endpoint:** `GET /api/v1/assessment/result/{sessionId}`
**Controller:** `ResultController`
**Service:** `ResultGenerationService` → `ResultGenerationServiceImpl`

**What happens:**
1. Load all `InterviewAnswerEntity` for the session
2. Group answers by category
3. Average score per category → save each as `CategoryScoreEntity`
4. Build final summary prompt with all Q&A pairs
5. Call `GeminiClient.generateContent(...)` with final summary prompt
6. Parse Gemini response:
   ```json
   {
     "overallScore": 7.2,
     "recommendation": "HIRE",
     "strengths": ["Strong Java fundamentals", "Good system design knowledge"],
     "weaknesses": ["Weak on distributed systems", "Limited cloud experience"],
     "summary": "Candidate demonstrated solid backend skills with room for improvement in cloud-native topics."
   }
   ```
7. Save `InterviewResultEntity`
8. Return `InterviewResultResponseDto`:
   ```json
   {
     "sessionId": 101,
     "overallScore": 7.2,
     "recommendation": "HIRE",
     "strengths": ["Strong Java fundamentals"],
     "weaknesses": ["Weak on distributed systems"],
     "summary": "...",
     "categoryScores": [
       { "category": "TECHNICAL",        "score": 8.0, "remarks": "Strong" },
       { "category": "PROBLEM_SOLVING",  "score": 7.0, "remarks": "Good" },
       { "category": "COMMUNICATION",    "score": 6.5, "remarks": "Average" },
       { "category": "RESUME_RELEVANCE", "score": 7.5, "remarks": "Good" },
       { "category": "CODING",           "score": 7.0, "remarks": "Good" }
     ]
   }
   ```

---

## 5. Gemini Prompt Templates

All templates live in `PromptTemplateSamples.java`. Placeholders use `{{key}}` syntax and are filled by `PromptBuilderService`.

### Template 1 — Resume Skill Extraction
```
You are an expert resume parser.
Extract structured information from the following resume text.

Resume Text:
{{resumeText}}

Return ONLY a valid JSON object with these fields:
- candidateName (string)
- skills (array of strings)
- yearsOfExperience (integer)
- projectKeywords (array of strings)
- companyHistory (comma-separated string)

Return only JSON, no explanation.
```

### Template 2 — JD Skill Extraction
```
You are an expert job description analyzer.
Extract structured information from the following job description.

Job Description:
{{jobDescriptionText}}

Return ONLY a valid JSON object with these fields:
- roleApplied (string)
- mandatorySkills (array of strings)
- optionalSkills (array of strings)

Return only JSON, no explanation.
```

### Template 3 — Question Generation (already in codebase)
```
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
```

### Template 4 — Answer Evaluation
```
You are an expert technical interviewer evaluating a candidate's answer.

Question: {{questionText}}
Category: {{category}}
Difficulty: {{difficulty}}
Candidate Experience: {{yearsOfExperience}} years
Candidate Answer: {{answerText}}

Evaluate the answer and return ONLY a valid JSON object with:
- score (double, 0.0 to 10.0)
- feedback (string, 1-2 sentences)
- strengths (array of strings)
- improvements (array of strings)

Return only JSON, no explanation.
```

### Template 5 — Final Interview Summary
```
You are an expert technical interview evaluator.
Below is the complete interview transcript for a candidate.

Role Applied: {{roleApplied}}
Candidate Experience: {{yearsOfExperience}} years

Interview Q&A:
{{interviewTranscript}}

Category Scores:
{{categoryScores}}

Based on the above, return ONLY a valid JSON object with:
- overallScore (double, 0.0 to 10.0)
- recommendation (one of: HIRE, HOLD, REJECT)
- strengths (array of strings)
- weaknesses (array of strings)
- summary (string, 2-3 sentences)

Return only JSON, no explanation.
```

---

## 6. Category & Difficulty Strategy

### Category Rotation (Phase 1 — fixed order)

| Sequence Range | Category         |
|----------------|------------------|
| Questions 1–2  | TECHNICAL        |
| Questions 3–4  | PROBLEM_SOLVING  |
| Questions 5–6  | COMMUNICATION    |
| Questions 7–8  | RESUME_RELEVANCE |
| Questions 9–10 | CODING           |

For `maxQuestions` other than 10, distribute evenly using modulo on the category list.

### Difficulty Escalation (per session)

| Condition                          | Difficulty |
|------------------------------------|------------|
| First 2 questions                  | EASY       |
| Last 2 answers scored >= 7.0       | MEDIUM     |
| Last 2 answers scored >= 8.0       | HARD       |
| Last 2 answers scored < 5.0        | De-escalate one level |

---

## 7. Rating & Scoring Model

### Per-answer score
- Gemini returns a score from **0.0 to 10.0**
- Stored in `InterviewAnswerEntity.score`

### Per-category score
- Simple average of all answer scores in that category
- Stored in `CategoryScoreEntity.score`

### Overall score — weighted average

| Category         | Weight |
|------------------|--------|
| TECHNICAL        | 30%    |
| PROBLEM_SOLVING  | 25%    |
| CODING           | 20%    |
| RESUME_RELEVANCE | 15%    |
| COMMUNICATION    | 10%    |

### Recommendation thresholds

| Overall Score | Recommendation |
|---------------|----------------|
| >= 7.5        | HIRE           |
| 5.0 – 7.4     | HOLD           |
| < 5.0         | REJECT         |

> Note: Gemini also provides its own recommendation in the final summary prompt. The backend uses the **weighted score threshold** as the authoritative recommendation and Gemini's as a supporting narrative only.

---

## 8. API Endpoints Reference

| Method | Endpoint                                              | Description                              |
|--------|-------------------------------------------------------|------------------------------------------|
| POST   | `/api/v1/assessment/resume/upload`                    | Upload and parse resume                  |
| POST   | `/api/v1/assessment/jd/upload`                        | Upload and parse job description         |
| POST   | `/api/v1/assessment/interview/start`                  | Create session, get first question       |
| GET    | `/api/v1/assessment/interview/{sessionId}/next-question` | Get next AI-generated question        |
| POST   | `/api/v1/assessment/interview/{sessionId}/answer/text`| Submit text answer, get score            |
| POST   | `/api/v1/assessment/interview/{sessionId}/complete`   | Mark interview complete                  |
| GET    | `/api/v1/assessment/result/{sessionId}`               | Get full result with category scores     |

---

## 9. Database Entities Involved

| Entity                    | Purpose                                                              |
|---------------------------|----------------------------------------------------------------------|
| `ResumeEntity`            | Stores raw resume text and file metadata                             |
| `CandidateProfileEntity`  | Stores Gemini-extracted candidate skills and experience              |
| `JobDescriptionEntity`    | Stores raw JD text, role, and file metadata                          |
| `InterviewSessionEntity`  | Tracks session state (IN_PROGRESS / COMPLETED), mode, timestamps     |
| `InterviewQuestionEntity` | Each Gemini-generated question with category, difficulty, sequenceNo |
| `InterviewAnswerEntity`   | Candidate's text answer with Gemini score                            |
| `InterviewResultEntity`   | Final overall score, recommendation, strengths, weaknesses, summary  |
| `CategoryScoreEntity`     | Per-category averaged score linked to result                         |

---

## 10. Implementation Order

| #  | Class                              | What to implement                                                        |
|----|------------------------------------|--------------------------------------------------------------------------|
| 1  | `GeminiConfig`                     | `@ConfigurationProperties` binding for `gemini.*`                        |
| 2  | `RestClientConfig`                 | `RestClient` bean with Gemini base URL                                   |
| 3  | `GeminiClient`                     | HTTP POST to Gemini, extract `candidates[0].content.parts[0].text`       |
| 4  | `PromptTemplateSamples`            | Add 4 missing prompt templates                                           |
| 5  | `PromptBuilderServiceImpl`         | Fill `{{placeholders}}` for all 5 prompts                                |
| 6  | `SkillExtractionServiceImpl`       | Resume + JD skill extraction via Gemini                                  |
| 7  | `ResumeParsingServiceImpl`         | File read → skill extraction → persist Resume + CandidateProfile         |
| 8  | `JobDescriptionParsingServiceImpl` | File read → skill extraction → persist JobDescription                    |
| 9  | `GeminiQuestionServiceImpl`        | Build prompt → call Gemini → parse question JSON                         |
| 10 | `GeminiEvaluationServiceImpl`      | Build prompt → call Gemini → parse evaluation JSON                       |
| 11 | `InterviewSessionServiceImpl`      | Create session, category/difficulty logic                                |
| 12 | `QuestionOrchestrationServiceImpl` | Orchestrate question generation with full context                        |
| 13 | `ResultGenerationServiceImpl`      | Aggregate scores + Gemini final summary                                  |
| 14 | All `Impl` classes                 | Remove `abstract` keyword — Spring cannot instantiate abstract `@Service` |
| 15 | All Controllers                    | Inject and delegate to services                                          |

---

## 11. Configuration

### application.yml (Gemini section)
```yaml
gemini:
  base-url: https://generativelanguage.googleapis.com
  api-key: ${GEMINI_API_KEY}
  model: gemini-1.5-flash
```

### Environment variable required
```
GEMINI_API_KEY=your-actual-gemini-api-key
```

### Setting on Windows (development)
```cmd
set GEMINI_API_KEY=your-actual-gemini-api-key
```

### Setting in IntelliJ Run Configuration
```
Run > Edit Configurations > Environment Variables > GEMINI_API_KEY=your-actual-gemini-api-key
```

---

## Notes

- All Gemini prompts must instruct the model to **return only JSON** with no extra explanation to make parsing reliable.
- If Gemini returns malformed JSON, `GeminiClient` should throw an `ExternalServiceException` which is handled by `GlobalExceptionHandler`.
- Phase 2 will add voice input (Whisper STT) and multilingual support (LibreTranslate) on top of this same flow — the text evaluation pipeline remains unchanged.
