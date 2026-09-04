package com.example.jira_clone.service;

import com.example.jira_clone.dto.task.CommentDto;
import com.example.jira_clone.dto.task.CommentRequest;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByTaskId(String taskId);
    CommentDto createComment(String taskId, CommentRequest request, String userEmail);
    CommentDto updateComment(String id, CommentRequest request, String userEmail);
    void deleteComment(String id, String userEmail);
}
