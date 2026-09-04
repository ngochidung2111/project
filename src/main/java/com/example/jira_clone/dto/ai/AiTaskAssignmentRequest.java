package com.example.jira_clone.dto.ai;

import jakarta.validation.constraints.NotBlank;

public record AiTaskAssignmentRequest(
    @NotBlank(message = "Task ID is required")
    String taskId
) {}
