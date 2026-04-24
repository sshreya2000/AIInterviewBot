package com.company.aiinterview.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "ai_audit")
public class AiAuditEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String operation;
    @Column(columnDefinition = "TEXT")
    private String prompt;
    @Column(columnDefinition = "TEXT")
    private String response;
    private Instant createdAt;
}
