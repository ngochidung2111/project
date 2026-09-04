package com.example.jira_clone.dto.project;

import lombok.Builder;

@Builder
public record ProjectRoleDto(
    Long id,
    String code,
    String name
) {}
