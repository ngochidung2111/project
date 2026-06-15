package com.example.jira_clone.dto.user;

import com.example.jira_clone.dto.role.RoleDto;

public record UserDetailDto(
        String id,
        String email,
        String name,
        RoleDto role
) {

}