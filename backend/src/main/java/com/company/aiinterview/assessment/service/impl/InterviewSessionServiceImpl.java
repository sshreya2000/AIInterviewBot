package com.company.aiinterview.assessment.service.impl;

import com.company.aiinterview.ai.dto.request.EvaluateAnswerRequestDto;
import com.company.aiinterview.ai.service.GeminiEvaluationService;
import com.company.aiinterview.assessment.dto.request.StartInterviewRequestDto;
import com.company.aiinterview.assessment.dto.request.SubmitTextAnswerRequestDto;
import com.company.aiinterview.assessment.dto.response.StartInterviewResponseDto;
import com.company.aiinterview.assessment.dto.response.SubmitAnswerResponseDto;
import com.company.aiinterview.assessment.entity.InterviewAnswerEntity;
import com.company.aiinterview.assessment.entity.InterviewQuestionEntity;
import com.company.aiinterview.assessment.entity.InterviewSessionEntity;
import com.company.aiinterview.assessment.repository.InterviewAnswerRepository;
import com.company.aiinterview.assessment.repository.InterviewQuestionRepository;
import com.company.aiinterview.assessment.repository.InterviewSessionRepository;
import com.company.aiinterview.assessment.service.InterviewSessionService;
import com.company.aiinterview.assessment.service.QuestionOrchestrationService;
import com.company.aiinterview.assessment.service.ResultGenerationService;
import com.company.aiinterview.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewSessionServiceImpl implements InterviewSessionService {

    private final InterviewSessionRepository sessionRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final GeminiEvaluationService geminiEvaluationService;
    private final QuestionOrchestrationService questionOrchestrationService;
    private final ResultGenerationService resultGenerationService;

    @Override
    public StartInterviewResponseDto startSession(StartInterviewRequestDto request) {
        List<String> resumeSkills = request.getResumeSkills() != null ? request.getResumeSkills() : List.of();
        List<String> inferredSkills = request.getInferredSkills() != null ? request.getInferredSkills() : List.of();
        List<String> jdSkills = request.getJdSkills() != null ? request.getJdSkills() : List.of();

        InterviewSessionEntity session = sessionRepository.save(InterviewSessionEntity.builder()
                .candidateName(request.getCandidateName())
                .yearsOfExperience(request.getYearsOfExperience() != null ? request.getYearsOfExperience() : 0)
                .resumeSkillsCsv(String.join(",", resumeSkills))
                .inferredSkillsCsv(String.join(",", inferredSkills))
                .roleApplied(request.getRoleApplied())
                .jdSkillsCsv(String.join(",", jdSkills))
                .mode(request.getInterviewMode())
                .status("IN_PROGRESS")
                .startedAt(Instant.now())
                .build());

        var firstQuestion = questionOrchestrationService.nextQuestion(session.getId());

        return StartInterviewResponseDto.builder()
                .sessionId(session.getId())
                .status(session.getStatus())
                .interviewMode(session.getMode())
                .firstQuestionCategory(firstQuestion.getCategory())
                .build();
    }

    @Override
    public SubmitAnswerResponseDto submitTextAnswer(Long sessionId, SubmitTextAnswerRequestDto request) {
        InterviewSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        InterviewQuestionEntity question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + request.getQuestionId()));

        var evaluation = geminiEvaluationService.evaluate(EvaluateAnswerRequestDto.builder()
                .question(question.getQuestionText())
                .answer(request.getAnswerText())
                .category(question.getCategory())
                .difficulty(question.getDifficulty())
                .yearsOfExperience(session.getYearsOfExperience() != null ? session.getYearsOfExperience() : 0)
                .build());

        answerRepository.save(InterviewAnswerEntity.builder()
                .sessionId(sessionId)
                .questionId(request.getQuestionId())
                .answerText(request.getAnswerText())
                .languageCode(request.getLanguageCode())
                .score(evaluation.getScore())
                .build());

        return SubmitAnswerResponseDto.builder()
                .sessionId(sessionId)
                .questionId(request.getQuestionId())
                .answerScore(evaluation.getScore())
                .feedbackSummary(evaluation.getFeedback())
                .followUpGenerated(false)
                .build();
    }

    @Override
    public void completeSession(Long sessionId) {
        InterviewSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));
        session.setStatus("COMPLETED");
        session.setCompletedAt(Instant.now());
        sessionRepository.save(session);
        resultGenerationService.generate(sessionId);
    }
}
