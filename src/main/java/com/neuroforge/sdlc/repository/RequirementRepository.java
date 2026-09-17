package com.neuroforge.sdlc.repository;

import com.neuroforge.sdlc.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    List<Requirement> findByProjectId(Long projectId);
}
