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
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String passwordHash;
    private String fullName;
    private Boolean active;
    private Instant createdAt;
}
