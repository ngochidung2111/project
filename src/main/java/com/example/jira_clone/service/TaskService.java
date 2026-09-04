package com.example.jira_clone.service;

import com.example.jira_clone.dto.auth.UserDto;
import com.example.jira_clone.dto.task.KanbanBoardDto;
import com.example.jira_clone.dto.task.TaskDto;
import com.example.jira_clone.dto.task.TaskRequest;

import java.util.List;

public interface TaskService {
    TaskDto getTaskById(String id);
    TaskDto createTask(TaskRequest request, String userEmail);
    TaskDto updateTask(String id, TaskRequest request);
    void deleteTask(String id);
    List<TaskDto> searchTasks(String projectId, String sprintId, String status, String priority, String assigneeId);
    TaskDto updateStatus(String id, String status);
    TaskDto updatePriority(String id, String priority);
    TaskDto moveToSprint(String id, String sprintId);
    KanbanBoardDto getKanbanBoard(String projectId);
    TaskDto moveTask(String id, String status);
    
    // Assignment methods
    List<UserDto> getAssignees(String taskId);
    void assignUser(String taskId, String userId);
    void removeAssignee(String taskId, String userId);
}

