package com.example.jira_clone.service;

import com.example.jira_clone.dto.task.AttachmentDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface AttachmentService {
    AttachmentDto uploadFile(String taskId, MultipartFile file, String userEmail);
    Resource downloadFile(String id);
    String getFileName(String id);
    void deleteFile(String id, String userEmail);
}
