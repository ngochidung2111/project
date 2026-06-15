package com.example.jira_clone.repository;

import com.example.jira_clone.entity.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, String> {
    List<TaskHistory> findByTaskId(String taskId);

    List<TaskHistory> findByTaskIdOrderByCreatedAtDesc(String taskId);
}
