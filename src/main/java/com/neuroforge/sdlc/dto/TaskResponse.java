package com.neuroforge.sdlc.dto;

import com.neuroforge.sdlc.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String taskName;
    private String description;
    private TaskStatus status;
    private Long projectId;
    private Long assignedToId;
    private String assignedToName;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}
