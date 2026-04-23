package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.InterviewQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestionEntity, Long> {
}
