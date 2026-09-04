package com.example.jira_clone.dto.skill;

import lombok.Builder;

@Builder
public record SkillDto(
    Long id,
    String name
) {}
