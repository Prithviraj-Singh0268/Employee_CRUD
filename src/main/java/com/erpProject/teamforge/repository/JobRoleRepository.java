package com.erpProject.teamforge.repository;

import com.erpProject.teamforge.entity.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRoleRepository extends JpaRepository<JobRole, Long> {
    boolean existsByTitleIgnoreCase(String title);
}