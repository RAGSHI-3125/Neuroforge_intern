package com.neuroforge.sdlc.service;

import com.neuroforge.sdlc.dto.TaskRequest;
import com.neuroforge.sdlc.dto.TaskResponse;
import com.neuroforge.sdlc.entity.Project;
import com.neuroforge.sdlc.entity.Task;
import com.neuroforge.sdlc.entity.TaskStatus;
import com.neuroforge.sdlc.entity.User;
import com.neuroforge.sdlc.exception.ResourceNotFoundException;
import com.neuroforge.sdlc.repository.ProjectRepository;
import com.neuroforge.sdlc.repository.TaskRepository;
import com.neuroforge.sdlc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskResponse create(TaskRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedToId()));
        }

        Task task = Task.builder()
                .taskName(request.getTaskName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .project(project)
                .assignedTo(assignedTo)
                .dueDate(request.getDueDate())
                .build();

        return toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getAll() {
        return taskRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<TaskResponse> getByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    public List<TaskResponse> getByAssignee(Long userId) {
        return taskRepository.findByAssignedToId(userId).stream().map(this::toResponse).toList();
    }

    public TaskResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = findEntity(id);
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedToId()));
        }

        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        task.setProject(project);
        task.setAssignedTo(assignedTo);
        task.setDueDate(request.getDueDate());

        return toResponse(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.delete(findEntity(id));
    }

    private Task findEntity(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    private TaskResponse toResponse(Task t) {
        return TaskResponse.builder()
                .id(t.getId())
                .taskName(t.getTaskName())
                .description(t.getDescription())
                .status(t.getStatus())
                .projectId(t.getProject().getId())
                .assignedToId(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .assignedToName(t.getAssignedTo() != null ? t.getAssignedTo().getFullName() : null)
                .dueDate(t.getDueDate())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
