package com.example.jira_clone.repository;

import com.example.jira_clone.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, String> {
    List<Sprint> findByProjectId(String projectId);

    List<Sprint> findByProjectIdAndStatus(String projectId, String status);
}
