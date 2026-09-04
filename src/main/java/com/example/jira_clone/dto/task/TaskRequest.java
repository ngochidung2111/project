package com.example.jira_clone.dto.task;

import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
    String projectId,
    String sprintId,
    @NotBlank(message = "Title is required")
    String title,
    String description,
    String priority,
    Integer storyPoint
) {}
