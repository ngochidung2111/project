package com.example.jira_clone.service;

import com.example.jira_clone.dto.project.ProjectRoleDto;

import java.util.List;

public interface ProjectRoleService {
    List<ProjectRoleDto> findAll();
}
