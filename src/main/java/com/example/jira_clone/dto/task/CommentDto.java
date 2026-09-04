package com.example.jira_clone.dto.task;

import com.example.jira_clone.dto.auth.UserDto;
import java.time.LocalDateTime;

public record CommentDto(
    String id,
    String taskId,
    UserDto user,
    String content,
    LocalDateTime createdAt
) {}
