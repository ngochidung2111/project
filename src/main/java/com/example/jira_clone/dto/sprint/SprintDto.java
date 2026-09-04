package com.example.jira_clone.dto.sprint;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record SprintDto(
    String id,
    String projectId,
    String name,
    String goal,
    LocalDate startDate,
    LocalDate endDate,
    String status
) {}
