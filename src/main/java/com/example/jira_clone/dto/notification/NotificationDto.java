package com.example.jira_clone.dto.notification;

import java.time.LocalDateTime;

public record NotificationDto(
    String id,
    String message,
    boolean isRead,
    LocalDateTime createdAt
) {}
