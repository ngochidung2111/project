package com.example.jira_clone.dto.ai;

import lombok.Builder;

@Builder
public record AiSprintPredictionResponse(
    int completionProbability,
    String riskLevel
) {}
