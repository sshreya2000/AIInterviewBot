package com.company.aiinterview.aiinterview.entity;

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
@Table(name = "interviewsession")
public class InterviewSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long candidateProfileId;
    private String mode;
    private String status;
    private Instant startedAt;
    private Instant completedAt;
}
