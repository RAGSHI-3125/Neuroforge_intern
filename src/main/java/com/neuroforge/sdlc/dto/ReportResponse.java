package com.neuroforge.sdlc.dto;

import com.neuroforge.sdlc.entity.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private Long id;
    private String reportType;
    private Long bugId;
    private Severity severity;
    private String description;
    private Long testCaseId;
    private Long projectId;
    private String generatedByName;
    private LocalDateTime createdAt;
}
