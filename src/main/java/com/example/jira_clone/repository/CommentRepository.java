package com.example.jira_clone.repository;

import com.example.jira_clone.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByTaskId(String taskId);

    List<Comment> findByUserId(String userId);
}
