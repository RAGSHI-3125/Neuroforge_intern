package com.neuroforge.sdlc.controller;

import com.neuroforge.sdlc.dto.UserResponse;
import com.neuroforge.sdlc.entity.Role;
import com.neuroforge.sdlc.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Read-only user lookups, e.g. for populating manager/assignee dropdowns")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAll(@RequestParam(required = false) Role role) {
        if (role != null) {
            return userService.getByRole(role);
        }
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }
}
