package com.example.jira_clone.dto.task;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
    @NotBlank(message = "Comment content cannot be blank")
    String content
) {}
