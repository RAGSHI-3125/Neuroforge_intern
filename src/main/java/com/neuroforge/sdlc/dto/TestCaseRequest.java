package com.neuroforge.sdlc.dto;

import com.neuroforge.sdlc.entity.TestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestCaseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private TestStatus status;

    private Integer noOfTestCases;

    @NotNull(message = "Task id is required")
    private Long taskId;

    @NotNull(message = "createdBy (QA user id) is required")
    private Long createdById;
}
