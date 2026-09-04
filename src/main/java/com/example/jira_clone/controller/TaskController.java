package com.example.jira_clone.controller;

import com.example.jira_clone.dto.auth.UserDto;
import com.example.jira_clone.dto.task.AssignUserRequest;
import com.example.jira_clone.dto.task.TaskHistoryDto;
import com.example.jira_clone.dto.task.TaskDto;
import com.example.jira_clone.dto.task.TaskRequest;
import com.example.jira_clone.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;
    private final com.example.jira_clone.service.TaskHistoryService taskHistoryService;

    public TaskController(TaskService taskService, com.example.jira_clone.service.TaskHistoryService taskHistoryService) {
        this.taskService = taskService;
        this.taskHistoryService = taskHistoryService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task detail")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<TaskDto> updateTask(@PathVariable String id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
    }

    @GetMapping
    @Operation(summary = "Search tasks")
    public ResponseEntity<List<TaskDto>> searchTasks(
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String sprintId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String assigneeId) {
        return ResponseEntity.ok(taskService.searchTasks(projectId, sprintId, status, priority, assigneeId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<TaskDto> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Update task priority")
    public ResponseEntity<TaskDto> updatePriority(@PathVariable String id, @RequestBody Map<String, String> body) {
        String priority = body.get("priority");
        return ResponseEntity.ok(taskService.updatePriority(id, priority));
    }

    @PatchMapping("/{id}/sprint")
    @Operation(summary = "Move task to a sprint")
    public ResponseEntity<TaskDto> moveToSprint(@PathVariable String id, @RequestBody Map<String, String> body) {
        String sprintId = body.get("sprintId");
        return ResponseEntity.ok(taskService.moveToSprint(id, sprintId));
    }

    @GetMapping("/{taskId}/assignees")
    @Operation(summary = "Get task assignees")
    public ResponseEntity<List<UserDto>> getAssignees(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getAssignees(taskId));
    }

    @PostMapping("/{taskId}/assignees")
    @Operation(summary = "Assign a user to task")
    public ResponseEntity<Void> assignUser(@PathVariable String taskId, @Valid @RequestBody AssignUserRequest request) {
        taskService.assignUser(taskId, request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{taskId}/assignees/{userId}")
    @Operation(summary = "Remove assignee from task")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAssignee(@PathVariable String taskId, @PathVariable String userId) {
        taskService.removeAssignee(taskId, userId);
    }

    @GetMapping("/{taskId}/histories")
    @Operation(summary = "Get task history log")
    public ResponseEntity<List<TaskHistoryDto>> getTaskHistory(@PathVariable String taskId) {
        return ResponseEntity.ok(taskHistoryService.getHistoryForTask(taskId));
    }
}
