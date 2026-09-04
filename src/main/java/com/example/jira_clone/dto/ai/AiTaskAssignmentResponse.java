package com.example.jira_clone.dto.ai;

import lombok.Builder;

@Builder
public record AiTaskAssignmentResponse(
    String userId,
    String userName,
    double score
) {}
