package com.neuroforge.sdlc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequirementRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String technicalStack;

    @NotNull(message = "Project id is required")
    private Long projectId;

    @NotNull(message = "createdBy (user id) is required")
    private Long createdById;
}
