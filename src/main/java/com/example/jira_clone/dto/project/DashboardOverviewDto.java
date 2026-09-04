package com.example.jira_clone.dto.project;

import lombok.Builder;
import java.util.Map;

@Builder
public record DashboardOverviewDto(
    long totalTasks,
    long completedTasks,
    double completionRate,
    Map<String, Long> tasksByStatus,
    Map<String, Long> tasksByPriority
) {}
