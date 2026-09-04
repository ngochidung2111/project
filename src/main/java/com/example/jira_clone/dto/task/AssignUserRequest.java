package com.example.jira_clone.dto.task;

import jakarta.validation.constraints.NotBlank;

public record AssignUserRequest(
    @NotBlank(message = "User ID is required")
    String userId
) {}
