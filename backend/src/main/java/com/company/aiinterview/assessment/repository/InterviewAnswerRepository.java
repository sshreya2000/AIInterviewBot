package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.InterviewAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswerEntity, Long> {
}
