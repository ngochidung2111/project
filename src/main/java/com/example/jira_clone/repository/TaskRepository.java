package com.example.jira_clone.repository;

import com.example.jira_clone.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByProjectId(String projectId);

    List<Task> findBySprintId(String sprintId);

    List<Task> findByProjectIdAndStatus(String projectId, String status);

    List<Task> findByCreatedBy(String createdById);
}
