package com.company.aiinterview.assessment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "interviewsession")
public class InterviewSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String candidateName;
    private Integer yearsOfExperience;
    private String roleApplied;
    @Column(columnDefinition = "TEXT")
    private String resumeSkillsCsv;
    @Column(columnDefinition = "TEXT")
    private String inferredSkillsCsv;
    @Column(columnDefinition = "TEXT")
    private String jdSkillsCsv;
    private String mode;
    private String status;
    private Instant startedAt;
    private Instant completedAt;
}
