package com.neuroforge.sdlc.controller;

import com.neuroforge.sdlc.dto.ReportRequest;
import com.neuroforge.sdlc.dto.ReportResponse;
import com.neuroforge.sdlc.service.ReportService;
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
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    // Matches UML: Project Manager has Generate Report()
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<ReportResponse> create(@Valid @RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAll(@RequestParam(required = false) Long projectId) {
        if (projectId != null) {
            return ResponseEntity.ok(reportService.getByProject(projectId));
        }
        return ResponseEntity.ok(reportService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getById(id));
    }

    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
