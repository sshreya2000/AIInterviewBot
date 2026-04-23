package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.CandidateProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfileEntity, Long> {
}
