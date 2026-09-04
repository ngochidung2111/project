package com.example.jira_clone.dto.user;

import lombok.Builder;

@Builder
public record UserSkillDto(
    Long skillId,
    String skillName,
    Integer level
) {}
