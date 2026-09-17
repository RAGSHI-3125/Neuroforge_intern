package com.neuroforge.sdlc.controller;

import com.neuroforge.sdlc.dto.TestCaseRequest;
import com.neuroforge.sdlc.dto.TestCaseResponse;
import com.neuroforge.sdlc.service.TestCaseService;
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
@RequestMapping("/api/testcases")
@RequiredArgsConstructor
@Tag(name = "Test Cases")
@SecurityRequirement(name = "bearerAuth")
public class TestCaseController {

    private final TestCaseService testCaseService;

    // Matches UML: QA has create testCase()
    @PreAuthorize("hasAnyRole('QA','ADMIN')")
    @PostMapping
    public ResponseEntity<TestCaseResponse> create(@Valid @RequestBody TestCaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testCaseService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<TestCaseResponse>> getAll(@RequestParam(required = false) Long taskId) {
        if (taskId != null) {
            return ResponseEntity.ok(testCaseService.getByTask(taskId));
        }
        return ResponseEntity.ok(testCaseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestCaseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(testCaseService.getById(id));
    }

    @PreAuthorize("hasAnyRole('QA','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TestCaseResponse> update(@PathVariable Long id, @Valid @RequestBody TestCaseRequest request) {
        return ResponseEntity.ok(testCaseService.update(id, request));
    }

    @PreAuthorize("hasAnyRole('QA','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        testCaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
