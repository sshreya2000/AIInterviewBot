package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.InterviewResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewResultRepository extends JpaRepository<InterviewResultEntity, Long> {
    Optional<InterviewResultEntity> findBySessionId(Long sessionId);
}
