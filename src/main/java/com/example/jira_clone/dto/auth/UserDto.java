package com.example.jira_clone.dto.auth;

public record UserDto(
        String id,
        String email,
        String fullName,
        String avatarUrl,
        String status) {
}