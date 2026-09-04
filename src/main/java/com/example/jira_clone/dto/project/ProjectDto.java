package com.example.jira_clone.dto.project;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ProjectDto(
    String id,
    String name,
    String description,
    String ownerId,
    String status,
    LocalDate startDate,
    LocalDate endDate,
    LocalDateTime createdAt
) {}
