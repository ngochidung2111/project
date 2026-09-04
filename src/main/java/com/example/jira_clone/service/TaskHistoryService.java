package com.example.jira_clone.service;

import com.example.jira_clone.dto.task.TaskHistoryDto;
import com.example.jira_clone.entity.Task;
import java.util.List;

public interface TaskHistoryService {
    void logChange(Task task, String fieldName, String oldValue, String newValue);
    List<TaskHistoryDto> getHistoryForTask(String taskId);
}
