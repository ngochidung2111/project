package com.example.jira_clone.dto.sprint;

import lombok.Builder;

@Builder
public record SprintMetricsDto(
    int totalTasks,
    int completedTasks,
    double completionRate,
    int totalStoryPoints,
    int completedStoryPoints
) {}
