package com.company.aiinterview.aiinterview.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "translation_audit")
public class TranslationAuditEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String sourceText;
    @Column(columnDefinition = "TEXT")
    private String translatedText;
}
