package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.InterviewResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewResultRepository extends JpaRepository<InterviewResultEntity, Long> {
}
