package com.example.jira_clone.controller;

import com.example.jira_clone.dto.project.ProjectRoleDto;
import com.example.jira_clone.service.ProjectRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project-roles")
@SecurityRequirement(name = "bearerAuth")
public class ProjectRoleController {

    private final ProjectRoleService projectRoleService;

    public ProjectRoleController(ProjectRoleService projectRoleService) {
        this.projectRoleService = projectRoleService;
    }

    @GetMapping
    @Operation(summary = "Get all available project roles")
    public ResponseEntity<List<ProjectRoleDto>> getProjectRoles() {
        return ResponseEntity.ok(projectRoleService.findAll());
    }
}
