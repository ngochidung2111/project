package com.example.jira_clone.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssignRoleRequest(
    @NotNull(message = "Role ID cannot be null")
    Long roleId
) {}
