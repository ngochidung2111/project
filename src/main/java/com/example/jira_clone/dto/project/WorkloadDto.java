package com.example.jira_clone.dto.project;

import lombok.Builder;

@Builder
public record WorkloadDto(
    String userId,
    String fullName,
    String avatarUrl,
    long taskCount,
    int storyPoints
) {}
