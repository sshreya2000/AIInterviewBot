package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {
}
