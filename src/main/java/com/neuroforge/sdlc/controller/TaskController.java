package com.neuroforge.sdlc.controller;

import com.neuroforge.sdlc.dto.TaskRequest;
import com.neuroforge.sdlc.dto.TaskResponse;
import com.neuroforge.sdlc.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    // Matches UML: Project Manager has Assign Task()
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assignedToId) {
        if (projectId != null) {
            return ResponseEntity.ok(taskService.getByProject(projectId));
        }
        if (assignedToId != null) {
            return ResponseEntity.ok(taskService.getByAssignee(assignedToId));
        }
        return ResponseEntity.ok(taskService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    // No @PreAuthorize here on purpose: any logged-in user (e.g. the assigned
    // developer) can update task status/details - not restricted in the UML.
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }

    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
