package com.example.jira_clone.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record ProjectRequest(
    @NotBlank(message = "Project name cannot be blank")
    String name,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    String status
) {}
