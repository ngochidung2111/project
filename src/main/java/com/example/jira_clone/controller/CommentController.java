package com.example.jira_clone.controller;

import com.example.jira_clone.dto.task.CommentDto;
import com.example.jira_clone.dto.task.CommentRequest;
import com.example.jira_clone.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/tasks/{taskId}/comments")
    @Operation(summary = "Get comments for a task")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable String taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }

    @PostMapping("/tasks/{taskId}/comments")
    @Operation(summary = "Create a comment on a task")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable String taskId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(taskId, request, authentication.getName()));
    }

    @PutMapping("/comments/{id}")
    @Operation(summary = "Update a comment")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable String id,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(id, request, authentication.getName()));
    }

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "Delete a comment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable String id, Authentication authentication) {
        commentService.deleteComment(id, authentication.getName());
    }
}
