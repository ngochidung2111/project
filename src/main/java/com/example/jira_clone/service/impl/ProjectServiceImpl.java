package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.project.ProjectDto;
import com.example.jira_clone.dto.project.ProjectOverviewDto;
import com.example.jira_clone.dto.project.ProjectRequest;
import com.example.jira_clone.entity.Project;
import com.example.jira_clone.entity.Task;
import com.example.jira_clone.repository.ProjectMemberRepository;
import com.example.jira_clone.repository.ProjectRepository;
import com.example.jira_clone.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findAll() {
        return projectRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findByUserId(String userId) {
        // Projects where user is owner or member
        // For simplicity, let's just return all projects the user is involved in
        List<String> projectIdsAsMember = projectMemberRepository.findByUserId(userId).stream()
                .map(pm -> pm.getProject().getId())
                .collect(Collectors.toList());
        
        return projectRepository.findAll().stream()
                .filter(p -> p.getOwnerId().equals(userId) || projectIdsAsMember.contains(p.getId()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto findById(String id) {
        return projectRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id));
    }

    @Override
    @Transactional
    public ProjectDto createProject(ProjectRequest request, String ownerId) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .ownerId(ownerId)
                .status(request.status() != null ? request.status() : "ACTIVE")
                .build();
        
        Project savedProject = projectRepository.save(project);
        return toDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectDto updateProject(String id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id));
        
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        
        Project updatedProject = projectRepository.save(project);
        return toDto(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(String id) {
        if (!projectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectOverviewDto getProjectOverview(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id));
        
        List<Task> tasks = project.getTasks();
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream()
                .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
                .count();
        
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        
        return ProjectOverviewDto.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .completionRate(completionRate)
                .build();
    }

    private ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwnerId())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
