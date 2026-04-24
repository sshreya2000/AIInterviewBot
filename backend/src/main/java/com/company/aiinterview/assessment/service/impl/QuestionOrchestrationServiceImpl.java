package com.company.aiinterview.assessment.service.impl;

import com.company.aiinterview.ai.dto.request.GenerateQuestionRequestDto;
import com.company.aiinterview.ai.service.GeminiQuestionService;
import com.company.aiinterview.assessment.dto.response.NextQuestionResponseDto;
import com.company.aiinterview.assessment.entity.InterviewQuestionEntity;
import com.company.aiinterview.assessment.entity.InterviewSessionEntity;
import com.company.aiinterview.assessment.repository.InterviewAnswerRepository;
import com.company.aiinterview.assessment.repository.InterviewQuestionRepository;
import com.company.aiinterview.assessment.repository.InterviewSessionRepository;
import com.company.aiinterview.assessment.service.QuestionOrchestrationService;
import com.company.aiinterview.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionOrchestrationServiceImpl implements QuestionOrchestrationService {

    private static final List<String> CATEGORY_ROTATION =
            List.of("TECHNICAL", "TECHNICAL", "PROBLEM_SOLVING", "PROBLEM_SOLVING",
                    "COMMUNICATION", "COMMUNICATION", "RESUME_RELEVANCE", "RESUME_RELEVANCE",
                    "CODING", "CODING");

    private final InterviewSessionRepository sessionRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final GeminiQuestionService geminiQuestionService;

    @Override
    public NextQuestionResponseDto nextQuestion(Long sessionId) {
        InterviewSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        List<InterviewQuestionEntity> existing = questionRepository.findBySessionId(sessionId);
        int seqNo = existing.size() + 1;
        String category = CATEGORY_ROTATION.get(Math.min(seqNo - 1, CATEGORY_ROTATION.size() - 1));
        String difficulty = resolveDifficulty(sessionId, seqNo);

        List<String> previousTexts = existing.stream()
                .map(InterviewQuestionEntity::getQuestionText)
                .collect(Collectors.toList());

        List<String> resumeSkills = session.getResumeSkillsCsv() != null && !session.getResumeSkillsCsv().isBlank()
                ? Arrays.asList(session.getResumeSkillsCsv().split(","))
                : List.of();

        List<String> inferredSkills = session.getInferredSkillsCsv() != null && !session.getInferredSkillsCsv().isBlank()
                ? Arrays.asList(session.getInferredSkillsCsv().split(","))
                : List.of();

        List<String> jdSkills = session.getJdSkillsCsv() != null && !session.getJdSkillsCsv().isBlank()
                ? Arrays.asList(session.getJdSkillsCsv().split(","))
                : List.of();

        var response = geminiQuestionService.generateQuestion(GenerateQuestionRequestDto.builder()
                .resumeSkills(resumeSkills)
                .inferredSkills(inferredSkills)
                .jdSkills(jdSkills)
                .yearsOfExperience(session.getYearsOfExperience() != null ? session.getYearsOfExperience() : 0)
                .category(category)
                .difficulty(difficulty)
                .previousQuestions(previousTexts)
                .answerHistorySummary(buildAnswerSummary(sessionId))
                .build());

        InterviewQuestionEntity saved = questionRepository.save(InterviewQuestionEntity.builder()
                .sessionId(sessionId)
                .category(response.getCategory())
                .difficulty(response.getDifficulty())
                .questionText(response.getQuestionText())
                .sequenceNo(seqNo)
                .build());

        return NextQuestionResponseDto.builder()
                .questionId(saved.getId())
                .category(saved.getCategory())
                .difficulty(saved.getDifficulty())
                .questionText(saved.getQuestionText())
                .sequenceNo(saved.getSequenceNo())
                .build();
    }

    private String resolveDifficulty(Long sessionId, int seqNo) {
        if (seqNo <= 2) return "EASY";
        List<Double> recentScores = answerRepository.findTop2BySessionIdOrderByIdDesc(sessionId)
                .stream().map(a -> a.getScore() != null ? a.getScore() : 0.0).collect(Collectors.toList());
        if (recentScores.size() < 2) return "EASY";
        double avg = recentScores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        if (avg >= 8.0) return "HARD";
        if (avg >= 7.0) return "MEDIUM";
        return "EASY";
    }

    private String buildAnswerSummary(Long sessionId) {
        return answerRepository.findTop3BySessionIdOrderByIdDesc(sessionId).stream()
                .map(a -> "Score: " + a.getScore())
                .collect(Collectors.joining("; "));
    }
}
