package com.example.jira_clone.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddMemberRequest(
    @NotBlank(message = "User ID cannot be blank")
    String userId
) {}
