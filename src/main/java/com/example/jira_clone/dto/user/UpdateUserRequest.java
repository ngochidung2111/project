package com.example.jira_clone.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
    @NotBlank(message = "Full name cannot be blank")
    String fullName,

    String avatarUrl,

    @NotBlank(message = "Status cannot be blank")
    String status
) {}
