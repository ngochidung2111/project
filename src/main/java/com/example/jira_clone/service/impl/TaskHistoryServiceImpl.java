package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.task.TaskHistoryDto;
import com.example.jira_clone.entity.Task;
import com.example.jira_clone.entity.TaskHistory;
import com.example.jira_clone.entity.User;
import com.example.jira_clone.repository.TaskHistoryRepository;
import com.example.jira_clone.repository.UserRepository;
import com.example.jira_clone.service.TaskHistoryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;
    private final UserRepository userRepository;

    public TaskHistoryServiceImpl(TaskHistoryRepository taskHistoryRepository, UserRepository userRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void logChange(Task task, String fieldName, String oldValue, String newValue) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return;
        }
        String userEmail = auth.getName();
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return;
        }

        TaskHistory history = TaskHistory.builder()
                .task(task)
                .user(user)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();

        taskHistoryRepository.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskHistoryDto> getHistoryForTask(String taskId) {
        return taskHistoryRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(h -> new TaskHistoryDto(
                        h.getId(),
                        h.getTask().getId(),
                        h.getUser().getId(),
                        h.getUser().getFullName(),
                        h.getFieldName(),
                        h.getOldValue(),
                        h.getNewValue(),
                        h.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
