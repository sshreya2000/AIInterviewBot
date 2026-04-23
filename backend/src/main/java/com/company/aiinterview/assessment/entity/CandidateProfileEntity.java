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
@Table(name = "candidateprofile")
public class CandidateProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String candidateName;
    private Integer yearsOfExperience;
    private String skillsCsv;
    private String projectKeywordsCsv;
    private String companyHistory;
}
