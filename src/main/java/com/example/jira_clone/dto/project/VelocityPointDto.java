package com.example.jira_clone.dto.project;

import lombok.Builder;

@Builder
public record VelocityPointDto(
    String sprintName,
    Integer estimated,
    Integer completed
) {}
