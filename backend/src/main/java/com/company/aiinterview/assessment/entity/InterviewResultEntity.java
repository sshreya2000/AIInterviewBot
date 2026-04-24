package com.company.aiinterview.assessment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "interviewresult")
public class InterviewResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sessionId;
    private Double overallScore;
    private String recommendation;
    private String strengths;
    private String weaknesses;
    private String summary;
}
