package com.example.jira_clone.dto.project;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProjectMemberDto(
    String id,
    String userId,
    String fullName,
    String email,
    String avatarUrl,
    LocalDateTime joinedAt,
    List<ProjectRoleDto> roles
) {}
