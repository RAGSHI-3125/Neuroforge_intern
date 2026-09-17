package com.neuroforge.sdlc.service;

import com.neuroforge.sdlc.dto.ReportRequest;
import com.neuroforge.sdlc.dto.ReportResponse;
import com.neuroforge.sdlc.entity.Project;
import com.neuroforge.sdlc.entity.Report;
import com.neuroforge.sdlc.entity.TestCase;
import com.neuroforge.sdlc.entity.User;
import com.neuroforge.sdlc.exception.ResourceNotFoundException;
import com.neuroforge.sdlc.repository.ProjectRepository;
import com.neuroforge.sdlc.repository.ReportRepository;
import com.neuroforge.sdlc.repository.TestCaseRepository;
import com.neuroforge.sdlc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final TestCaseRepository testCaseRepository;
    private final UserRepository userRepository;

    public ReportResponse create(ReportRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
        User generatedBy = userRepository.findById(request.getGeneratedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getGeneratedById()));

        TestCase testCase = null;
        if (request.getTestCaseId() != null) {
            testCase = testCaseRepository.findById(request.getTestCaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("TestCase not found with id: " + request.getTestCaseId()));
        }

        Report report = Report.builder()
                .reportType(request.getReportType())
                .bugId(request.getBugId())
                .severity(request.getSeverity())
                .description(request.getDescription())
                .testCase(testCase)
                .project(project)
                .generatedBy(generatedBy)
                .build();

        return toResponse(reportRepository.save(report));
    }

    public List<ReportResponse> getAll() {
        return reportRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ReportResponse> getByProject(Long projectId) {
        return reportRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    public ReportResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public void delete(Long id) {
        reportRepository.delete(findEntity(id));
    }

    private Report findEntity(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
    }

    private ReportResponse toResponse(Report r) {
        return ReportResponse.builder()
                .id(r.getId())
                .reportType(r.getReportType())
                .bugId(r.getBugId())
                .severity(r.getSeverity())
                .description(r.getDescription())
                .testCaseId(r.getTestCase() != null ? r.getTestCase().getId() : null)
                .projectId(r.getProject().getId())
                .generatedByName(r.getGeneratedBy().getFullName())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
