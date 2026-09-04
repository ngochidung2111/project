package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.project.ProjectRoleDto;
import com.example.jira_clone.repository.ProjectRoleRepository;
import com.example.jira_clone.service.ProjectRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectRoleServiceImpl implements ProjectRoleService {

    private final ProjectRoleRepository projectRoleRepository;

    public ProjectRoleServiceImpl(ProjectRoleRepository projectRoleRepository) {
        this.projectRoleRepository = projectRoleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectRoleDto> findAll() {
        return projectRoleRepository.findAll().stream()
                .map(role -> new ProjectRoleDto(role.getId(), role.getCode(), role.getName()))
                .collect(Collectors.toList());
    }
}
