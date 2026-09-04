package com.example.jira_clone.dto.project;

import lombok.Builder;

@Builder
public record ProjectOverviewDto(
    long totalTasks,
    long completedTasks,
    double completionRate
) {}
