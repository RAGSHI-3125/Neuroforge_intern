package com.neuroforge.sdlc.service;

import com.neuroforge.sdlc.dto.RequirementRequest;
import com.neuroforge.sdlc.dto.RequirementResponse;
import com.neuroforge.sdlc.entity.Project;
import com.neuroforge.sdlc.entity.Requirement;
import com.neuroforge.sdlc.entity.User;
import com.neuroforge.sdlc.exception.ResourceNotFoundException;
import com.neuroforge.sdlc.repository.ProjectRepository;
import com.neuroforge.sdlc.repository.RequirementRepository;
import com.neuroforge.sdlc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public RequirementResponse create(RequirementRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getCreatedById()));

        Requirement requirement = Requirement.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .technicalStack(request.getTechnicalStack())
                .project(project)
                .createdBy(createdBy)
                .build();

        return toResponse(requirementRepository.save(requirement));
    }

    public List<RequirementResponse> getAll() {
        return requirementRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<RequirementResponse> getByProject(Long projectId) {
        return requirementRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    public RequirementResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public RequirementResponse update(Long id, RequirementRequest request) {
        Requirement requirement = findEntity(id);
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        requirement.setTitle(request.getTitle());
        requirement.setDescription(request.getDescription());
        requirement.setTechnicalStack(request.getTechnicalStack());
        requirement.setProject(project);

        return toResponse(requirementRepository.save(requirement));
    }

    public void delete(Long id) {
        requirementRepository.delete(findEntity(id));
    }

    private Requirement findEntity(Long id) {
        return requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found with id: " + id));
    }

    private RequirementResponse toResponse(Requirement r) {
        return RequirementResponse.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .technicalStack(r.getTechnicalStack())
                .projectId(r.getProject().getId())
                .createdByName(r.getCreatedBy().getFullName())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
