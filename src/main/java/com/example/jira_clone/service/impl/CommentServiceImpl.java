package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.auth.UserDto;
import com.example.jira_clone.dto.task.CommentDto;
import com.example.jira_clone.dto.task.CommentRequest;
import com.example.jira_clone.entity.Comment;
import com.example.jira_clone.entity.Task;
import com.example.jira_clone.entity.User;
import com.example.jira_clone.repository.CommentRepository;
import com.example.jira_clone.repository.TaskRepository;
import com.example.jira_clone.repository.UserRepository;
import com.example.jira_clone.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final com.example.jira_clone.service.NotificationService notificationService;

    public CommentServiceImpl(CommentRepository commentRepository, TaskRepository taskRepository,
                              UserRepository userRepository, com.example.jira_clone.service.NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByTaskId(String taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return commentRepository.findByTaskId(taskId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(String taskId, CommentRequest request, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Comment comment = Comment.builder()
                .task(task)
                .user(user)
                .content(request.content())
                .build();

        Comment saved = commentRepository.save(comment);

        // Notify assignees (excluding the commenter themselves)
        for (com.example.jira_clone.entity.TaskAssignment assignment : task.getTaskAssignments()) {
            if (!assignment.getUser().getId().equals(user.getId())) {
                notificationService.createNotification(assignment.getUser(), 
                    user.getFullName() + " commented on task '" + task.getTitle() + "': " + request.content());
            }
        }

        return toDto(saved);
    }

    @Override
    @Transactional
    public CommentDto updateComment(String id, CommentRequest request, String userEmail) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this comment");
        }

        comment.setContent(request.content());
        return toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(String id, String userEmail) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentDto toDto(Comment comment) {
        UserDto userDto = new UserDto(
                comment.getUser().getId(),
                comment.getUser().getEmail(),
                comment.getUser().getFullName(),
                comment.getUser().getAvatarUrl(),
                comment.getUser().getStatus()
        );
        return new CommentDto(
                comment.getId(),
                comment.getTask().getId(),
                userDto,
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
