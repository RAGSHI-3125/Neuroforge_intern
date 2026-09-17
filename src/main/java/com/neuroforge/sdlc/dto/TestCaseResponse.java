package com.neuroforge.sdlc.dto;

import com.neuroforge.sdlc.entity.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResponse {
    private Long id;
    private String title;
    private TestStatus status;
    private Integer noOfTestCases;
    private Long taskId;
    private String createdByName;
    private LocalDateTime createdAt;
}
