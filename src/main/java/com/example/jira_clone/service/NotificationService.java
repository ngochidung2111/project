package com.example.jira_clone.service;

import com.example.jira_clone.dto.notification.NotificationDto;
import com.example.jira_clone.entity.User;
import java.util.List;

public interface NotificationService {
    List<NotificationDto> getNotificationsForUser(String userEmail);
    NotificationDto markAsRead(String id, String userEmail);
    void markAllAsRead(String userEmail);
    void createNotification(User recipient, String message);
}
