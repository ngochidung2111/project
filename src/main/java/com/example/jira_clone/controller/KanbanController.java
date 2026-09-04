package com.example.jira_clone.controller;

import com.example.jira_clone.dto.task.KanbanBoardDto;
import com.example.jira_clone.dto.task.TaskDto;
import com.example.jira_clone.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
public class KanbanController {

    private final TaskService taskService;

    public KanbanController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/projects/{projectId}/board")
    @Operation(summary = "Get Kanban board for a project")
    public ResponseEntity<KanbanBoardDto> getBoard(@PathVariable String projectId) {
        return ResponseEntity.ok(taskService.getKanbanBoard(projectId));
    }

    @PatchMapping("/tasks/{id}/move")
    @Operation(summary = "Move task to another status")
    public ResponseEntity<TaskDto> moveTask(@PathVariable String id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(taskService.moveTask(id, status));
    }
}
