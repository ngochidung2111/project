package com.example.jira_clone.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateProfileRequest(
    @NotBlank(message = "Full name cannot be blank")
    String fullName,
    String avatarUrl
) {}
