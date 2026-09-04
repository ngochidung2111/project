package com.example.jira_clone.dto.project;

import lombok.Builder;

@Builder
public record BurndownPointDto(
    String date,
    Integer remainingPoints,
    Integer idealPoints
) {}
