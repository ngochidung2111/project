package com.example.jira_clone.repository;

import com.example.jira_clone.entity.ProjectMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRoleRepository extends JpaRepository<ProjectMemberRole, String> {
    List<ProjectMemberRole> findByProjectMemberId(String projectMemberId);
}
