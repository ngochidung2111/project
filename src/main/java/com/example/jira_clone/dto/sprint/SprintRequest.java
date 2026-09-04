package com.example.jira_clone.dto.sprint;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record SprintRequest(
    @NotBlank(message = "Sprint name is required")
    String name,
    String goal,
    LocalDate startDate,
    LocalDate endDate
) {}
