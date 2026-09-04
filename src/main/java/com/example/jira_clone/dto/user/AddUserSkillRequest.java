package com.example.jira_clone.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddUserSkillRequest(
    @NotNull(message = "Skill ID cannot be null")
    Long skillId,

    @NotNull(message = "Level cannot be null")
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 5, message = "Level must be at most 5")
    Integer level
) {}
