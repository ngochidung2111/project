package com.example.jira_clone.dto.task;

import java.time.LocalDateTime;

public record AttachmentDto(
    String id,
    String taskId,
    String uploadedByUserId,
    String uploadedByUserName,
    String fileName,
    String fileUrl,
    LocalDateTime createdAt
) {}
