package com.example.jira_clone.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SkillRequest(
    @NotBlank(message = "Skill name cannot be blank")
    String name
) {}
