package com.example.jira_clone.controller;

import com.example.jira_clone.dto.sprint.SprintDto;
import com.example.jira_clone.dto.sprint.SprintMetricsDto;
import com.example.jira_clone.dto.sprint.SprintRequest;
import com.example.jira_clone.service.SprintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @GetMapping("/projects/{projectId}/sprints")
    @Operation(summary = "Get all sprints for a project")
    public ResponseEntity<List<SprintDto>> getSprintsByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(sprintService.getSprintsByProject(projectId));
    }

    @GetMapping("/sprints/{id}")
    @Operation(summary = "Get sprint detail")
    public ResponseEntity<SprintDto> getSprintById(@PathVariable String id) {
        return ResponseEntity.ok(sprintService.getSprintById(id));
    }

    @PostMapping("/projects/{projectId}/sprints")
    @Operation(summary = "Create a new sprint")
    public ResponseEntity<SprintDto> createSprint(@PathVariable String projectId, @Valid @RequestBody SprintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sprintService.createSprint(projectId, request));
    }

    @PutMapping("/sprints/{id}")
    @Operation(summary = "Update an existing sprint")
    public ResponseEntity<SprintDto> updateSprint(@PathVariable String id, @Valid @RequestBody SprintRequest request) {
        return ResponseEntity.ok(sprintService.updateSprint(id, request));
    }

    @DeleteMapping("/sprints/{id}")
    @Operation(summary = "Delete a sprint")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSprint(@PathVariable String id) {
        sprintService.deleteSprint(id);
    }

    @PostMapping("/sprints/{id}/start")
    @Operation(summary = "Start a sprint")
    public ResponseEntity<SprintDto> startSprint(@PathVariable String id) {
        return ResponseEntity.ok(sprintService.startSprint(id));
    }

    @PostMapping("/sprints/{id}/complete")
    @Operation(summary = "Complete a sprint")
    public ResponseEntity<SprintDto> completeSprint(@PathVariable String id) {
        return ResponseEntity.ok(sprintService.completeSprint(id));
    }

    @GetMapping("/sprints/{id}/metrics")
    @Operation(summary = "Get sprint metrics")
    public ResponseEntity<SprintMetricsDto> getSprintMetrics(@PathVariable String id) {
        return ResponseEntity.ok(sprintService.getSprintMetrics(id));
    }
}
