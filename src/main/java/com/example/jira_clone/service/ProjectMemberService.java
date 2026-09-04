package com.example.jira_clone.service;

import com.example.jira_clone.dto.project.AddMemberRequest;
import com.example.jira_clone.dto.project.AssignRoleRequest;
import com.example.jira_clone.dto.project.ProjectMemberDto;
import com.example.jira_clone.dto.project.ProjectRoleDto;

import java.util.List;

public interface ProjectMemberService {
    List<ProjectMemberDto> findMembersByProjectId(String projectId);
    ProjectMemberDto addMember(String projectId, AddMemberRequest request);
    void removeMember(String projectId, String memberId);
    void assignRole(String projectId, String memberId, AssignRoleRequest request);
    void removeRole(String projectId, String memberId, Long roleId);
}
