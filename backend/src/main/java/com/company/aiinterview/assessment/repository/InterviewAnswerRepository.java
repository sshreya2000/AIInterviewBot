package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.InterviewAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswerEntity, Long> {
    List<InterviewAnswerEntity> findBySessionId(Long sessionId);
    List<InterviewAnswerEntity> findTop2BySessionIdOrderByIdDesc(Long sessionId);
    List<InterviewAnswerEntity> findTop3BySessionIdOrderByIdDesc(Long sessionId);
}
