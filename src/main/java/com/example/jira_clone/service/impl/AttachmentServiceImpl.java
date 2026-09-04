package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.task.AttachmentDto;
import com.example.jira_clone.entity.Attachment;
import com.example.jira_clone.entity.Task;
import com.example.jira_clone.entity.User;
import com.example.jira_clone.repository.AttachmentRepository;
import com.example.jira_clone.repository.TaskRepository;
import com.example.jira_clone.repository.UserRepository;
import com.example.jira_clone.service.AttachmentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    private final Path fileStorageLocation;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    @Transactional
    public AttachmentDto uploadFile(String taskId, MultipartFile file, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename contains invalid path sequence");
        }

        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store file. Please try again!", ex);
        }

        Attachment attachment = Attachment.builder()
                .task(task)
                .uploadedBy(user)
                .fileName(originalFileName)
                .fileUrl(storedFileName)
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return toDto(savedAttachment);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadFile(String id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));

        try {
            Path filePath = this.fileStorageLocation.resolve(attachment.getFileUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found " + attachment.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found " + attachment.getFileName(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getFileName(String id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
        return attachment.getFileName();
    }

    @Override
    @Transactional
    public void deleteFile(String id, String userEmail) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));

        if (!attachment.getUploadedBy().getEmail().equalsIgnoreCase(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this attachment");
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(attachment.getFileUrl()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // ignore
        }

        attachmentRepository.delete(attachment);
    }

    private AttachmentDto toDto(Attachment attachment) {
        String downloadUrl = "/api/v1/attachments/" + attachment.getId();
        return new AttachmentDto(
                attachment.getId(),
                attachment.getTask().getId(),
                attachment.getUploadedBy().getId(),
                attachment.getUploadedBy().getFullName(),
                attachment.getFileName(),
                downloadUrl,
                attachment.getCreatedAt()
        );
    }
}
