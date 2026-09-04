package com.example.jira_clone.service;

import com.example.jira_clone.dto.project.ProjectDto;
import com.example.jira_clone.dto.project.ProjectOverviewDto;
import com.example.jira_clone.dto.project.ProjectRequest;

import java.util.List;

public interface ProjectService {
    List<ProjectDto> findAll();
    List<ProjectDto> findByUserId(String userId);
    ProjectDto findById(String id);
    ProjectDto createProject(ProjectRequest request, String ownerId);
    ProjectDto updateProject(String id, ProjectRequest request);
    void deleteProject(String id);
    ProjectOverviewDto getProjectOverview(String id);
}
