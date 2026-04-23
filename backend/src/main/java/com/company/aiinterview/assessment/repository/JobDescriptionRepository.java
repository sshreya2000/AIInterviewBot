package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.JobDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDescriptionRepository extends JpaRepository<JobDescriptionEntity, Long> {
}
