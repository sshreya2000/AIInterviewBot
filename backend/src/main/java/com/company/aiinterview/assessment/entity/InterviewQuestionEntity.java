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
@Table(name = "interviewquestion")
public class InterviewQuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sessionId;
    private String category;
    private String difficulty;
    private String questionText;
    private Integer sequenceNo;
}
