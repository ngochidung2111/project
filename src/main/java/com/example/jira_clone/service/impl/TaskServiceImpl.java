package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.auth.UserDto;
import com.example.jira_clone.dto.task.KanbanBoardDto;
import com.example.jira_clone.dto.task.TaskDto;
import com.example.jira_clone.dto.task.TaskRequest;
import com.example.jira_clone.entity.Project;
import com.example.jira_clone.entity.Sprint;
import com.example.jira_clone.entity.Task;
import com.example.jira_clone.entity.User;
import com.example.jira_clone.repository.ProjectRepository;
import com.example.jira_clone.repository.SprintRepository;
import com.example.jira_clone.repository.TaskRepository;
import com.example.jira_clone.repository.UserRepository;
import com.example.jira_clone.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;
    private final com.example.jira_clone.repository.TaskAssignmentRepository taskAssignmentRepository;
    private final com.example.jira_clone.service.TaskHistoryService taskHistoryService;
    private final com.example.jira_clone.service.NotificationService notificationService;

    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository,
                           SprintRepository sprintRepository, UserRepository userRepository,
                           com.example.jira_clone.repository.TaskAssignmentRepository taskAssignmentRepository,
                           com.example.jira_clone.service.TaskHistoryService taskHistoryService,
                           com.example.jira_clone.service.NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.taskHistoryService = taskHistoryService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(String id) {
        return taskRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    @Override
    @Transactional
    public TaskDto createTask(TaskRequest request, String userEmail) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        
        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Sprint sprint = null;
        if (request.sprintId() != null && !request.sprintId().isEmpty()) {
            sprint = sprintRepository.findById(request.sprintId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));
        }

        Task task = Task.builder()
                .project(project)
                .sprint(sprint)
                .title(request.title())
                .description(request.description())
                .priority(request.priority() != null ? request.priority() : "MEDIUM")
                .storyPoint(request.storyPoint())
                .createdBy(creator)
                .status("BACKLOG")
                .build();

        return toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDto updateTask(String id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        String oldTitle = task.getTitle();
        String oldDesc = task.getDescription();
        String oldPriority = task.getPriority();
        Integer oldStoryPoint = task.getStoryPoint();
        Sprint oldSprint = task.getSprint();

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setStoryPoint(request.storyPoint());

        if (request.sprintId() != null && !request.sprintId().isEmpty()) {
            Sprint sprint = sprintRepository.findById(request.sprintId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));
            task.setSprint(sprint);
        } else {
            task.setSprint(null);
        }

        Task savedTask = taskRepository.save(task);

        if (!java.util.Objects.equals(oldTitle, request.title())) {
            taskHistoryService.logChange(savedTask, "title", oldTitle, request.title());
        }
        if (!java.util.Objects.equals(oldDesc, request.description())) {
            taskHistoryService.logChange(savedTask, "description", oldDesc, request.description());
        }
        if (!java.util.Objects.equals(oldPriority, request.priority())) {
            taskHistoryService.logChange(savedTask, "priority", oldPriority, request.priority());
        }
        if (!java.util.Objects.equals(oldStoryPoint, request.storyPoint())) {
            taskHistoryService.logChange(savedTask, "storyPoint", 
                oldStoryPoint == null ? null : oldStoryPoint.toString(), 
                request.storyPoint() == null ? null : request.storyPoint().toString());
        }
        Sprint newSprint = savedTask.getSprint();
        String oldSprintName = oldSprint == null ? null : oldSprint.getName();
        String newSprintName = newSprint == null ? null : newSprint.getName();
        if (!java.util.Objects.equals(oldSprintName, newSprintName)) {
            taskHistoryService.logChange(savedTask, "sprint", oldSprintName, newSprintName);
        }

        return toDto(savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(String id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> searchTasks(String projectId, String sprintId, String status, String priority, String assigneeId) {
        return taskRepository.findAll().stream()
                .filter(t -> projectId == null || projectId.isEmpty() || t.getProject().getId().equals(projectId))
                .filter(t -> sprintId == null || sprintId.isEmpty() || (t.getSprint() != null && t.getSprint().getId().equals(sprintId)))
                .filter(t -> status == null || status.isEmpty() || t.getStatus().equalsIgnoreCase(status))
                .filter(t -> priority == null || priority.isEmpty() || t.getPriority().equalsIgnoreCase(priority))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDto updateStatus(String id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        String oldStatus = task.getStatus();
        String newStatus = status.toUpperCase();
        task.setStatus(newStatus);
        Task savedTask = taskRepository.save(task);
        if (!oldStatus.equalsIgnoreCase(newStatus)) {
            taskHistoryService.logChange(savedTask, "status", oldStatus, newStatus);
            
            // Notify assignees
            for (com.example.jira_clone.entity.TaskAssignment assignment : savedTask.getTaskAssignments()) {
                notificationService.createNotification(assignment.getUser(), 
                    "Task '" + savedTask.getTitle() + "' status has been updated to " + newStatus);
            }
        }
        return toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDto updatePriority(String id, String priority) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        String oldPriority = task.getPriority();
        String newPriority = priority.toUpperCase();
        task.setPriority(newPriority);
        Task savedTask = taskRepository.save(task);
        if (!oldPriority.equalsIgnoreCase(newPriority)) {
            taskHistoryService.logChange(savedTask, "priority", oldPriority, newPriority);
        }
        return toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDto moveToSprint(String id, String sprintId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        
        Sprint oldSprint = task.getSprint();
        if (sprintId != null && !sprintId.isEmpty()) {
            Sprint sprint = sprintRepository.findById(sprintId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));
            task.setSprint(sprint);
        } else {
            task.setSprint(null);
        }
        
        Task savedTask = taskRepository.save(task);
        String oldSprintName = oldSprint == null ? null : oldSprint.getName();
        String newSprintName = savedTask.getSprint() == null ? null : savedTask.getSprint().getName();
        if (!java.util.Objects.equals(oldSprintName, newSprintName)) {
            taskHistoryService.logChange(savedTask, "sprint", oldSprintName, newSprintName);
        }
        return toDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public KanbanBoardDto getKanbanBoard(String projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        
        return KanbanBoardDto.builder()
                .todo(tasks.stream().filter(t -> "TODO".equalsIgnoreCase(t.getStatus())).map(this::toDto).collect(Collectors.toList()))
                .inProgress(tasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).map(this::toDto).collect(Collectors.toList()))
                .testing(tasks.stream().filter(t -> "TESTING".equalsIgnoreCase(t.getStatus())).map(this::toDto).collect(Collectors.toList()))
                .done(tasks.stream().filter(t -> "DONE".equalsIgnoreCase(t.getStatus())).map(this::toDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public TaskDto moveTask(String id, String status) {
        return updateStatus(id, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAssignees(String taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return taskAssignmentRepository.findByTaskId(taskId).stream()
                .map(assignment -> toUserDto(assignment.getUser()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignUser(String taskId, String userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<com.example.jira_clone.entity.TaskAssignment> existing = taskAssignmentRepository.findByTaskIdAndUserId(taskId, userId);
        if (existing.isEmpty()) {
            com.example.jira_clone.entity.TaskAssignment assignment = com.example.jira_clone.entity.TaskAssignment.builder()
                    .task(task)
                    .user(user)
                    .build();
            taskAssignmentRepository.save(assignment);
            taskHistoryService.logChange(task, "assignee", null, user.getFullName());
            
            // Send notification
            notificationService.createNotification(user, "You have been assigned to task: " + task.getTitle());
        }
    }

    @Override
    @Transactional
    public void removeAssignee(String taskId, String userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<com.example.jira_clone.entity.TaskAssignment> existing = taskAssignmentRepository.findByTaskIdAndUserId(taskId, userId);
        if (!existing.isEmpty()) {
            taskAssignmentRepository.deleteAll(existing);
            taskHistoryService.logChange(task, "assignee", user.getFullName(), null);
        }
    }

    private TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .projectId(task.getProject().getId())
                .sprintId(task.getSprint() != null ? task.getSprint().getId() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .storyPoint(task.getStoryPoint())
                .status(task.getStatus())
                .createdBy(toUserDto(task.getCreatedBy()))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getStatus()
        );
    }
}
