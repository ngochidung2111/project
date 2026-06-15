package com.example.jira_clone.repository;

import com.example.jira_clone.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, String> {
    List<TaskAssignment> findByTaskId(String taskId);

    List<TaskAssignment> findByUserId(String userId);

    List<TaskAssignment> findByTaskIdAndUserId(String taskId, String userId);
}
