package com.company.aiinterview.assessment.repository;

import com.company.aiinterview.assessment.entity.CategoryScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryScoreRepository extends JpaRepository<CategoryScoreEntity, Long> {
    List<CategoryScoreEntity> findByResultId(Long resultId);
}
