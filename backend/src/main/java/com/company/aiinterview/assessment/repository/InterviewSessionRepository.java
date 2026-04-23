package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.InterviewSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSessionEntity, Long> {
}
