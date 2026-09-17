package com.neuroforge.sdlc.service;

import com.neuroforge.sdlc.dto.TestCaseRequest;
import com.neuroforge.sdlc.dto.TestCaseResponse;
import com.neuroforge.sdlc.entity.Task;
import com.neuroforge.sdlc.entity.TestCase;
import com.neuroforge.sdlc.entity.TestStatus;
import com.neuroforge.sdlc.entity.User;
import com.neuroforge.sdlc.exception.ResourceNotFoundException;
import com.neuroforge.sdlc.repository.TaskRepository;
import com.neuroforge.sdlc.repository.TestCaseRepository;
import com.neuroforge.sdlc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TestCaseResponse create(TestCaseRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getCreatedById()));

        TestCase testCase = TestCase.builder()
                .title(request.getTitle())
                .status(request.getStatus() != null ? request.getStatus() : TestStatus.PENDING)
                .noOfTestCases(request.getNoOfTestCases() != null ? request.getNoOfTestCases() : 1)
                .task(task)
                .createdBy(createdBy)
                .build();

        return toResponse(testCaseRepository.save(testCase));
    }

    public List<TestCaseResponse> getAll() {
        return testCaseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<TestCaseResponse> getByTask(Long taskId) {
        return testCaseRepository.findByTaskId(taskId).stream().map(this::toResponse).toList();
    }

    public TestCaseResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public TestCaseResponse update(Long id, TestCaseRequest request) {
        TestCase testCase = findEntity(id);
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

        testCase.setTitle(request.getTitle());
        if (request.getStatus() != null) {
            testCase.setStatus(request.getStatus());
        }
        if (request.getNoOfTestCases() != null) {
            testCase.setNoOfTestCases(request.getNoOfTestCases());
        }
        testCase.setTask(task);

        return toResponse(testCaseRepository.save(testCase));
    }

    public void delete(Long id) {
        testCaseRepository.delete(findEntity(id));
    }

    private TestCase findEntity(Long id) {
        return testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase not found with id: " + id));
    }

    private TestCaseResponse toResponse(TestCase tc) {
        return TestCaseResponse.builder()
                .id(tc.getId())
                .title(tc.getTitle())
                .status(tc.getStatus())
                .noOfTestCases(tc.getNoOfTestCases())
                .taskId(tc.getTask().getId())
                .createdByName(tc.getCreatedBy().getFullName())
                .createdAt(tc.getCreatedAt())
                .build();
    }
}
