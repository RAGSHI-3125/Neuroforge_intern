package com.neuroforge.sdlc.dto;

import com.neuroforge.sdlc.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Task name is required")
    private String taskName;

    private String description;

    private TaskStatus status;

    @NotNull(message = "Project id is required")
    private Long projectId;

    private Long assignedToId; // optional

    private LocalDate dueDate;
}
