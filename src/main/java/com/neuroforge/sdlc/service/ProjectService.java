package com.neuroforge.sdlc.service;

import com.neuroforge.sdlc.dto.ProjectRequest;
import com.neuroforge.sdlc.dto.ProjectResponse;
import com.neuroforge.sdlc.entity.Project;
import com.neuroforge.sdlc.entity.ProjectStatus;
import com.neuroforge.sdlc.entity.User;
import com.neuroforge.sdlc.exception.ResourceNotFoundException;
import com.neuroforge.sdlc.repository.ProjectRepository;
import com.neuroforge.sdlc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse create(ProjectRequest request) {
        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));

        Project project = Project.builder()
                .projectName(request.getProjectName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : ProjectStatus.PLANNING)
                .manager(manager)
                .build();

        return toResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> getAll() {
        return projectRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProjectResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = findEntity(id);
        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));

        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        project.setManager(manager);

        return toResponse(projectRepository.save(project));
    }

    public void delete(Long id) {
        Project project = findEntity(id);
        projectRepository.delete(project);
    }

    private Project findEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private ProjectResponse toResponse(Project p) {
        return ProjectResponse.builder()
                .id(p.getId())
                .projectName(p.getProjectName())
                .description(p.getDescription())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus())
                .managerId(p.getManager().getId())
                .managerName(p.getManager().getFullName())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
