package com.company.aiinterview.auth.repository;

import com.company.aiinterview.auth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
