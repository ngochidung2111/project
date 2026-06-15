package com.example.jira_clone.repository;

import com.example.jira_clone.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    List<Attachment> findByTaskId(String taskId);

    List<Attachment> findByUploadedById(String uploadedById);
}
