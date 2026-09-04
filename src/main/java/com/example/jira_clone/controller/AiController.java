package com.example.jira_clone.controller;

import com.example.jira_clone.dto.ai.AiProjectSummaryResponse;
import com.example.jira_clone.dto.ai.AiSprintPredictionResponse;
import com.example.jira_clone.dto.ai.AiTaskAssignmentRequest;
import com.example.jira_clone.dto.ai.AiTaskAssignmentResponse;
import com.example.jira_clone.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/task-assignment")
    @Operation(summary = "Get AI recommendation for task assignee")
    public ResponseEntity<AiTaskAssignmentResponse> getTaskAssignmentSuggestion(
            @Valid @RequestBody AiTaskAssignmentRequest request) {
        return ResponseEntity.ok(aiService.getTaskAssignmentSuggestion(request.taskId()));
    }

    @GetMapping("/sprints/{sprintId}/prediction")
    @Operation(summary = "Get AI prediction for sprint completion success probability")
    public ResponseEntity<AiSprintPredictionResponse> getSprintPrediction(@PathVariable String sprintId) {
        return ResponseEntity.ok(aiService.getSprintPrediction(sprintId));
    }

    @GetMapping("/projects/{projectId}/summary")
    @Operation(summary = "Get AI generated natural language summary of the project state")
    public ResponseEntity<AiProjectSummaryResponse> getProjectSummary(@PathVariable String projectId) {
        return ResponseEntity.ok(aiService.getProjectSummary(projectId));
    }
}
