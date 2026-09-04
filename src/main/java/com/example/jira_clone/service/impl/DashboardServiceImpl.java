package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.project.DashboardOverviewDto;
import com.example.jira_clone.dto.project.BurndownPointDto;
import com.example.jira_clone.dto.project.VelocityPointDto;
import com.example.jira_clone.dto.project.WorkloadDto;
import com.example.jira_clone.entity.*;
import com.example.jira_clone.repository.ProjectMemberRepository;
import com.example.jira_clone.repository.ProjectRepository;
import com.example.jira_clone.repository.SprintRepository;
import com.example.jira_clone.repository.TaskRepository;
import com.example.jira_clone.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public DashboardServiceImpl(ProjectRepository projectRepository, TaskRepository taskRepository,
                                SprintRepository sprintRepository, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.sprintRepository = sprintRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardOverviewDto getDashboardOverview(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> "DONE".equalsIgnoreCase(t.getStatus())).count();
        double completionRate = totalTasks == 0 ? 0.0 : (double) completedTasks / totalTasks * 100.0;

        Map<String, Long> tasksByStatus = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        Map<String, Long> tasksByPriority = tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        for (String status : List.of("TODO", "IN_PROGRESS", "TESTING", "DONE", "BACKLOG")) {
            tasksByStatus.putIfAbsent(status, 0L);
        }
        for (String priority : List.of("LOW", "MEDIUM", "HIGH", "CRITICAL")) {
            tasksByPriority.putIfAbsent(priority, 0L);
        }

        return DashboardOverviewDto.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .completionRate(completionRate)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BurndownPointDto> getBurndownChart(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        List<Sprint> activeSprints = sprintRepository.findByProjectIdAndStatus(projectId, "ACTIVE");
        Sprint sprint = activeSprints.isEmpty() ? null : activeSprints.get(0);

        if (sprint == null) {
            List<Sprint> allSprints = sprintRepository.findByProjectId(projectId);
            if (!allSprints.isEmpty()) {
                sprint = allSprints.get(0);
            }
        }

        List<BurndownPointDto> points = new ArrayList<>();
        if (sprint == null) {
            return points;
        }

        LocalDate startDate = sprint.getStartDate() != null ? sprint.getStartDate() : LocalDate.now().minusDays(7);
        LocalDate endDate = sprint.getEndDate() != null ? sprint.getEndDate() : LocalDate.now().plusDays(7);
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) totalDays = 1;

        List<Task> tasks = taskRepository.findBySprintId(sprint.getId());
        int totalPoints = tasks.stream()
                .mapToInt(t -> t.getStoryPoint() != null ? t.getStoryPoint() : 0)
                .sum();

        for (int i = 0; i <= totalDays; i++) {
            LocalDate current = startDate.plusDays(i);
            if (current.isAfter(LocalDate.now())) {
                int ideal = (int) Math.max(0, totalPoints - (i * (double) totalPoints / totalDays));
                points.add(new BurndownPointDto(current.toString(), null, ideal));
            } else {
                int completedOnOrBefore = 0;
                for (Task t : tasks) {
                    if ("DONE".equalsIgnoreCase(t.getStatus())) {
                        LocalDate completeDate = t.getUpdatedAt().toLocalDate();
                        if (!completeDate.isAfter(current)) {
                            completedOnOrBefore += (t.getStoryPoint() != null ? t.getStoryPoint() : 0);
                        }
                    }
                }
                int remaining = Math.max(0, totalPoints - completedOnOrBefore);
                int ideal = (int) Math.max(0, totalPoints - (i * (double) totalPoints / totalDays));
                points.add(new BurndownPointDto(current.toString(), remaining, ideal));
            }
        }

        return points;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VelocityPointDto> getVelocityChart(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        List<Sprint> sprints = sprintRepository.findByProjectId(projectId);
        List<VelocityPointDto> velocityPoints = new ArrayList<>();

        for (Sprint s : sprints) {
            List<Task> tasks = taskRepository.findBySprintId(s.getId());
            int estimated = tasks.stream()
                    .mapToInt(t -> t.getStoryPoint() != null ? t.getStoryPoint() : 0)
                    .sum();
            int completed = tasks.stream()
                    .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
                    .mapToInt(t -> t.getStoryPoint() != null ? t.getStoryPoint() : 0)
                    .sum();

            velocityPoints.add(new VelocityPointDto(s.getName(), estimated, completed));
        }

        return velocityPoints;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkloadDto> getTeamWorkload(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        List<WorkloadDto> workloadList = new ArrayList<>();

        for (ProjectMember member : members) {
            User user = member.getUser();
            long taskCount = 0;
            int storyPoints = 0;

            for (Task task : tasks) {
                boolean isAssigned = task.getTaskAssignments().stream()
                        .anyMatch(assignment -> assignment.getUser().getId().equals(user.getId()));
                if (isAssigned) {
                    taskCount++;
                    storyPoints += (task.getStoryPoint() != null ? task.getStoryPoint() : 0);
                }
            }

            workloadList.add(new WorkloadDto(
                    user.getId(),
                    user.getFullName(),
                    user.getAvatarUrl(),
                    taskCount,
                    storyPoints
            ));
        }

        return workloadList;
    }
}
