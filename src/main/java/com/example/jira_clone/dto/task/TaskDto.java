package com.example.jira_clone.dto.task;

import com.example.jira_clone.dto.auth.UserDto;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record TaskDto(
    String id,
    String projectId,
    String sprintId,
    String title,
    String description,
    String priority,
    Integer storyPoint,
    String status,
    UserDto createdBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
