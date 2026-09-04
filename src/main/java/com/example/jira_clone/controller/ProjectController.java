package com.example.jira_clone.controller;

import com.example.jira_clone.dto.auth.UserDto;
import com.example.jira_clone.dto.project.ProjectDto;
import com.example.jira_clone.dto.project.ProjectOverviewDto;
import com.example.jira_clone.dto.project.ProjectRequest;
import com.example.jira_clone.service.AuthService;
import com.example.jira_clone.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;
    private final AuthService authService;

    public ProjectController(ProjectService projectService, AuthService authService) {
        this.projectService = projectService;
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "Get all projects for the current user")
    public ResponseEntity<List<ProjectDto>> getProjects(Authentication authentication) {
        UserDto currentUser = authService.me(authentication.getName());
        return ResponseEntity.ok(projectService.findByUserId(currentUser.id()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project detail by ID")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable String id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectDto> createProject(Authentication authentication, @Valid @RequestBody ProjectRequest request) {
        UserDto currentUser = authService.me(authentication.getName());
        ProjectDto createdProject = projectService.createProject(request, currentUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable String id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
    }

    @GetMapping("/{id}/overview")
    @Operation(summary = "Get project overview metrics")
    public ResponseEntity<ProjectOverviewDto> getProjectOverview(@PathVariable String id) {
        return ResponseEntity.ok(projectService.getProjectOverview(id));
    }
}
