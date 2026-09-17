package com.neuroforge.sdlc.controller;

import com.neuroforge.sdlc.dto.RequirementRequest;
import com.neuroforge.sdlc.dto.RequirementResponse;
import com.neuroforge.sdlc.service.RequirementService;
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
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
@Tag(name = "Requirements")
@SecurityRequirement(name = "bearerAuth")
public class RequirementController {

    private final RequirementService requirementService;

    // Matches UML: Business Analyst has Create Requirement()
    @PreAuthorize("hasAnyRole('BUSINESS_ANALYST','ADMIN')")
    @PostMapping
    public ResponseEntity<RequirementResponse> create(@Valid @RequestBody RequirementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requirementService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RequirementResponse>> getAll(
            @RequestParam(required = false) Long projectId) {
        if (projectId != null) {
            return ResponseEntity.ok(requirementService.getByProject(projectId));
        }
        return ResponseEntity.ok(requirementService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequirementResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(requirementService.getById(id));
    }

    @PreAuthorize("hasAnyRole('BUSINESS_ANALYST','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RequirementResponse> update(@PathVariable Long id, @Valid @RequestBody RequirementRequest request) {
        return ResponseEntity.ok(requirementService.update(id, request));
    }

    @PreAuthorize("hasAnyRole('BUSINESS_ANALYST','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        requirementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
