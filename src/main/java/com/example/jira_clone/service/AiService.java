package com.example.jira_clone.service;

import com.example.jira_clone.dto.ai.AiTaskAssignmentResponse;
import com.example.jira_clone.dto.ai.AiSprintPredictionResponse;
import com.example.jira_clone.dto.ai.AiProjectSummaryResponse;

public interface AiService {
    AiTaskAssignmentResponse getTaskAssignmentSuggestion(String taskId);
    AiSprintPredictionResponse getSprintPrediction(String sprintId);
    AiProjectSummaryResponse getProjectSummary(String projectId);
}
