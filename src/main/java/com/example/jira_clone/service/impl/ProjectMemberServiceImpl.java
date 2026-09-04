package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.project.AddMemberRequest;
import com.example.jira_clone.dto.project.AssignRoleRequest;
import com.example.jira_clone.dto.project.ProjectMemberDto;
import com.example.jira_clone.dto.project.ProjectRoleDto;
import com.example.jira_clone.entity.*;
import com.example.jira_clone.repository.*;
import com.example.jira_clone.service.ProjectMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectMemberRoleRepository projectMemberRoleRepository;

    public ProjectMemberServiceImpl(
            ProjectMemberRepository projectMemberRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            ProjectRoleRepository projectRoleRepository,
            ProjectMemberRoleRepository projectMemberRoleRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectRoleRepository = projectRoleRepository;
        this.projectMemberRoleRepository = projectMemberRoleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberDto> findMembersByProjectId(String projectId) {
        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectMemberDto addMember(String projectId, AddMemberRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (projectMemberRepository.findByProjectIdAndUserId(projectId, request.userId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(user)
                .build();

        ProjectMember savedMember = projectMemberRepository.save(member);
        return toDto(savedMember);
    }

    @Override
    @Transactional
    public void removeMember(String projectId, String memberId) {
        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
        
        if (!member.getProject().getId().equals(projectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member does not belong to this project");
        }

        projectMemberRepository.delete(member);
    }

    @Override
    @Transactional
    public void assignRole(String projectId, String memberId, AssignRoleRequest request) {
        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
        ProjectRole role = projectRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        if (!member.getProject().getId().equals(projectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member does not belong to this project");
        }

        // Check if role already assigned
        boolean alreadyAssigned = member.getProjectMemberRoles().stream()
                .anyMatch(pmr -> pmr.getProjectRole().getId().equals(request.roleId()));
        
        if (alreadyAssigned) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already assigned to this member");
        }

        ProjectMemberRole pmr = ProjectMemberRole.builder()
                .projectMember(member)
                .projectRole(role)
                .build();
        
        projectMemberRoleRepository.save(pmr);
    }

    @Override
    @Transactional
    public void removeRole(String projectId, String memberId, Long roleId) {
        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        if (!member.getProject().getId().equals(projectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member does not belong to this project");
        }

        ProjectMemberRole pmr = member.getProjectMemberRoles().stream()
                .filter(r -> r.getProjectRole().getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found for this member"));

        projectMemberRoleRepository.delete(pmr);
    }

    private ProjectMemberDto toDto(ProjectMember member) {
        List<ProjectRoleDto> roles = member.getProjectMemberRoles().stream()
                .map(pmr -> new ProjectRoleDto(
                        pmr.getProjectRole().getId(),
                        pmr.getProjectRole().getCode(),
                        pmr.getProjectRole().getName()
                ))
                .collect(Collectors.toList());

        return ProjectMemberDto.builder()
                .id(member.getId())
                .userId(member.getUser().getId())
                .fullName(member.getUser().getFullName())
                .email(member.getUser().getEmail())
                .avatarUrl(member.getUser().getAvatarUrl())
                .joinedAt(member.getJoinedAt())
                .roles(roles)
                .build();
    }
}
