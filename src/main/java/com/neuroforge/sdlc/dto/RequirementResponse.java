package com.neuroforge.sdlc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequirementResponse {
    private Long id;
    private String title;
    private String description;
    private String technicalStack;
    private Long projectId;
    private String createdByName;
    private LocalDateTime createdAt;
}
