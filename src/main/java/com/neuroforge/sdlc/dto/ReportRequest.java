package com.neuroforge.sdlc.dto;

import com.neuroforge.sdlc.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequest {

    @NotBlank(message = "Report type is required")
    private String reportType;

    private Long bugId;

    private Severity severity;

    private String description;

    private Long testCaseId; // optional

    @NotNull(message = "Project id is required")
    private Long projectId;

    @NotNull(message = "generatedBy (user id) is required")
    private Long generatedById;
}
