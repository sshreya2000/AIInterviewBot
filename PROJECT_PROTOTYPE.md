# 1. High-level architecture summary
- **Architecture style:** Modular monolith (Spring Boot 3.x, Java 21) with feature-based modules: `auth`, `assessment`, `ai`, `voice`, `translation`.
- **Layering rule applied:** `controller -> service -> service.impl -> client -> repository -> entity`.
- **Core idea:** Interview questions are generated dynamically from Resume + JD + experience + history through Gemini (REST client style).
- **Modes:** Text interview and Voice interview (Piper TTS + Whisper STT), with optional multilingual translation via LibreTranslate.
- **Security:** JWT-based authentication with role support (ADMIN, INTERVIEWER, CANDIDATE).
- **Resulting output:** Category-wise scoring and recommendation (REJECT/BORDERLINE/SHORTLISTED/STRONG_HIRE).

# 2. Full backend folder structure
```text
backend/
├── pom.xml
├── src/main/resources/application.yml
└── src/main/java/com/company/aiinterview
    ├── AiInterviewApplication.java
    ├── config
    ├── security
    ├── exception
    ├── common
    │   ├── dto
    │   ├── enums
    │   ├── util
    │   └── constants
    ├── auth
    │   ├── controller
    │   ├── service
    │   ├── service/impl
    │   ├── repository
    │   ├── entity
    │   ├── dto/request
    │   ├── dto/response
    │   └── mapper
    ├── assessment
    │   ├── controller
    │   ├── service
    │   ├── service/impl
    │   ├── repository
    │   ├── entity
    │   ├── dto/request
    │   ├── dto/response
    │   ├── mapper
    │   └── client
    ├── ai
    │   ├── controller
    │   ├── service
    │   ├── service/impl
    │   ├── client
    │   ├── dto/request
    │   ├── dto/response
    │   ├── entity
    │   └── mapper
    ├── voice
    │   ├── controller
    │   ├── service
    │   ├── service/impl
    │   ├── client
    │   ├── dto/request
    │   ├── dto/response
    │   ├── entity
    │   └── mapper
    └── translation
        ├── controller
        ├── service
        ├── service/impl
        ├── client
        ├── dto/request
        ├── dto/response
        ├── entity
        └── mapper
```

# 3. Full frontend folder structure
```text
frontend/
├── package.json
├── index.html
└── src
    ├── App.jsx
    ├── main.jsx
    ├── styles.css
    ├── routes
    │   └── AppRoutes.jsx
    ├── api
    │   ├── httpClient.js
    │   ├── authApi.js
    │   ├── assessmentApi.js
    │   ├── voiceApi.js
    │   ├── translationApi.js
    │   └── aiApi.js
    ├── pages
    │   ├── LoginPage.jsx
    │   ├── DashboardPage.jsx
    │   ├── UploadDocumentsPage.jsx
    │   ├── InterviewSetupPage.jsx
    │   ├── TextInterviewPage.jsx
    │   ├── VoiceInterviewPage.jsx
    │   └── ResultPage.jsx
    └── components
        ├── ResumeUploader.jsx
        ├── JDUploader.jsx
        ├── InterviewModeSelector.jsx
        ├── QuestionCard.jsx
        ├── TextAnswerBox.jsx
        ├── VoiceRecorder.jsx
        ├── ResultScoreCard.jsx
        ├── CategoryBreakdown.jsx
        └── LanguageSelector.jsx
```

# 4. Package-wise class skeletons
> All required classes are created as starter skeletons with TODOs and signatures only.

- **Auth:** `AuthController`, `AuthService`, `AuthServiceImpl`, repositories, entities, DTOs, mapper.
- **Assessment:** resume/jd/interview/result controllers, parsing/orchestration/result services + impl, repositories, entities, DTOs, mapper.
- **AI:** `GeminiController`, generation/evaluation/prompt/skill services + impl, Gemini client, request/response DTOs, mapper.
- **Voice:** `VoiceController`, STT/TTS/VoiceInterview services + impl, Whisper/Piper clients, DTOs, mapper.
- **Translation:** `TranslationController`, service + impl, LibreTranslate client, DTOs, mapper.
- **Config/Security/Exception/Common:** all required core support skeleton classes.

Gemini integration styles documented in code:
- Preferred: Spring HTTP client wrapper (`GeminiClient`) using REST API.
- Optional note path: can be replaced with official Google Java SDK client in future implementation.

Prompt template example included:
- `ai/service/PromptTemplateSamples.java`

# 5. API design table
| Module | Method | Endpoint | Purpose |
|---|---|---|---|
| Auth | POST | `/api/v1/auth/register` | Register user |
| Auth | POST | `/api/v1/auth/login` | Login + JWT |
| Auth | GET | `/api/v1/auth/me` | Current user profile |
| Assessment | POST | `/api/v1/assessment/resume/upload` | Upload & parse resume |
| Assessment | POST | `/api/v1/assessment/jd/upload` | Upload & parse JD |
| Assessment | POST | `/api/v1/assessment/interview/start` | Create interview session |
| Assessment | GET | `/api/v1/assessment/interview/{sessionId}/next-question` | Next dynamic question |
| Assessment | POST | `/api/v1/assessment/interview/{sessionId}/answer/text` | Submit text answer |
| Assessment | POST | `/api/v1/assessment/interview/{sessionId}/complete` | Complete interview |
| Assessment | GET | `/api/v1/assessment/result/{sessionId}` | Final report |
| Voice | GET | `/api/v1/voice/interview/{sessionId}/question/{questionId}/audio` | TTS question audio |
| Voice | POST | `/api/v1/voice/interview/{sessionId}/answer/{questionId}/audio` | Upload voice answer |
| Voice | POST | `/api/v1/voice/stt` | Generic STT |
| Voice | POST | `/api/v1/voice/tts` | Generic TTS |
| Translation | POST | `/api/v1/translation/text` | Translate text |
| AI | POST | `/api/v1/ai/question` | Generate question |
| AI | POST | `/api/v1/ai/evaluate` | Evaluate answer |
| AI | POST | `/api/v1/ai/extract-skills` | Extract skills |

# 6. Sample JSON
## Resume upload response
```json
{
  "resumeId": 101,
  "fileName": "john_doe_resume.pdf",
  "extractedCandidateName": "John Doe",
  "derivedYearsOfExperience": 6,
  "extractedSkills": ["Java", "Spring Boot", "Kafka", "AWS", "Docker", "SQL"]
}
```
## JD upload response
```json
{
  "jobDescriptionId": 301,
  "roleApplied": "Senior Java Microservices Engineer",
  "mandatorySkills": ["Java", "Spring Boot", "Microservices", "REST", "SQL"],
  "optionalSkills": ["Kafka", "AWS", "Docker"]
}
```
## Start interview request
```json
{
  "candidateProfileId": 901,
  "jobDescriptionId": 301,
  "interviewMode": "VOICE",
  "preferredLanguage": "en",
  "maxQuestions": 12
}
```
## Start interview response
```json
{
  "sessionId": 5001,
  "status": "IN_PROGRESS",
  "interviewMode": "VOICE",
  "firstQuestionCategory": "Java"
}
```
## Next question response
```json
{
  "questionId": 70001,
  "category": "Microservices",
  "difficulty": "MEDIUM",
  "questionText": "Explain how you would design idempotent order-processing in event-driven microservices using Kafka.",
  "sequenceNo": 3
}
```
## Submit text answer request
```json
{
  "questionId": 70001,
  "answerText": "I would use a unique event key and deduplication store...",
  "languageCode": "en"
}
```
## Submit answer response
```json
{
  "sessionId": 5001,
  "questionId": 70001,
  "answerScore": 7.8,
  "feedbackSummary": "Good event-ordering approach; discuss failure recovery deeper.",
  "followUpGenerated": true
}
```
## Speech-to-text response
```json
{
  "transcript": "I use retries with exponential backoff and dead letter topics.",
  "detectedLanguage": "en",
  "confidence": 0.93
}
```
## Translation request/response
```json
{
  "text": "Describe your recent microservices project.",
  "sourceLanguage": "en",
  "targetLanguage": "hi",
  "format": "text"
}
```
```json
{
  "translatedText": "अपने हाल के माइक्रोसर्विसेज प्रोजेक्ट के बारे में बताइए।",
  "detectedLanguage": "en"
}
```
## Final result response
```json
{
  "sessionId": 5001,
  "communication": 8.0,
  "techstack": 7.5,
  "coding": 7.2,
  "problemSolving": 7.8,
  "confidence": 8.1,
  "resumeRelevance": 8.4,
  "overallScore": 7.8,
  "recommendation": "SHORTLISTED",
  "strengths": ["Strong Spring Boot fundamentals", "Clear communication"],
  "weaknesses": ["Limited deep-dive on Kafka partition strategy"],
  "summary": "Candidate meets most JD requirements and demonstrates practical experience.",
  "categoryScores": [
    {"category": "Java", "score": 8.0, "remarks": "Strong"},
    {"category": "Kafka", "score": 6.8, "remarks": "Needs improvement"}
  ]
}
```

# 7. Flow diagram
```text
[Resume Upload] + [JD Upload]
        |
        v
[Parse Documents -> Text Extraction]
        |
        v
[Candidate Profile Extraction]
(name, yoe, resume skills, projects, JD mandatory/optional skills)
        |
        v
[Interview Session + Plan Creation]
        |
        v
[Gemini Prompt Builder]
(yoe + resume skills + jd skills + previous questions + answer history + category + difficulty)
        |
        v
[Gemini Dynamic Question Generation]
        |
        +------------------------------+
        |                              |
        v                              v
   [Text Mode]                    [Voice Mode]
Question shown in UI         Q text -> Piper TTS -> audio
Answer typed                candidate audio upload
        |                   audio -> Whisper STT
        |                   optional Translate (to eval lang)
        +-----------+--------------+
                    v
            [AI Answer Evaluation]
                    |
                    v
            [Follow-up / Next Question]
                    |
                    v
            [Interview Complete Trigger]
                    |
                    v
      [Final Result Generation (category-wise)]
                    |
                    v
         [Recommendation + Summary + Report]
```

# 8. application.yml example
See `backend/src/main/resources/application.yml` for full prototype config skeleton covering:
- spring datasource/jpa
- security.jwt
- gemini
- whisper
- piper
- libre-translate
- springdoc

# 9. pom.xml dependency outline
See `backend/pom.xml` for dependency skeleton:
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- springdoc-openapi-starter-webmvc-ui
- postgresql
- jjwt-api / jjwt-impl / jjwt-jackson
- lombok
- spring-boot-starter-test

# 10. Notes for next implementation steps
1. Implement file extraction for PDF/DOCX in `assessment/client/DocumentParsingClient`.
2. Implement JWT filter/provider and user details wiring in `security/*`.
3. Implement Gemini REST request/response mapping in `ai/client/GeminiClient`.
4. Implement interview orchestration service methods with persistence transactions.
5. Implement Whisper/Piper process command execution and temp file cleanup.
6. Implement translation fallback and language-detection guardrails.
7. Add DB migrations (Flyway/Liquibase) for all entity tables.
8. Add validation annotations + global error responses.
9. Connect frontend components to API files and build end-to-end flow.
10. Add unit/integration tests for services and controller contracts.
