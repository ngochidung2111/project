package com.example.jira_clone.dto.task;

import java.time.LocalDateTime;

public record TaskHistoryDto(
    String id,
    String taskId,
    String userId,
    String userName,
    String fieldName,
    String oldValue,
    String newValue,
    LocalDateTime createdAt
) {}
