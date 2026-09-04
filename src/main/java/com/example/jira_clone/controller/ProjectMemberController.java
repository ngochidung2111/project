package com.example.jira_clone.controller;

import com.example.jira_clone.dto.project.AddMemberRequest;
import com.example.jira_clone.dto.project.AssignRoleRequest;
import com.example.jira_clone.dto.project.ProjectMemberDto;
import com.example.jira_clone.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
@SecurityRequirement(name = "bearerAuth")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @GetMapping
    @Operation(summary = "Get all members of a project")
    public ResponseEntity<List<ProjectMemberDto>> getMembers(@PathVariable String projectId) {
        return ResponseEntity.ok(projectMemberService.findMembersByProjectId(projectId));
    }

    @PostMapping
    @Operation(summary = "Add a member to a project")
    public ResponseEntity<ProjectMemberDto> addMember(@PathVariable String projectId, @Valid @RequestBody AddMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberService.addMember(projectId, request));
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "Remove a member from a project")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable String projectId, @PathVariable String memberId) {
        projectMemberService.removeMember(projectId, memberId);
    }

    @PostMapping("/{memberId}/roles")
    @Operation(summary = "Assign a role to a project member")
    public ResponseEntity<Void> assignRole(
            @PathVariable String projectId,
            @PathVariable String memberId,
            @Valid @RequestBody AssignRoleRequest request) {
        projectMemberService.assignRole(projectId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{memberId}/roles/{roleId}")
    @Operation(summary = "Remove a role from a project member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRole(
            @PathVariable String projectId,
            @PathVariable String memberId,
            @PathVariable Long roleId) {
        projectMemberService.removeRole(projectId, memberId, roleId);
    }
}
