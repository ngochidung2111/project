package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.ai.AiProjectSummaryResponse;
import com.example.jira_clone.dto.ai.AiSprintPredictionResponse;
import com.example.jira_clone.dto.ai.AiTaskAssignmentResponse;
import com.example.jira_clone.entity.*;
import com.example.jira_clone.repository.*;
import com.example.jira_clone.service.AiService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiServiceImpl implements AiService {

    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserSkillRepository userSkillRepository;

    public AiServiceImpl(TaskRepository taskRepository, SprintRepository sprintRepository,
                          ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository,
                          UserSkillRepository userSkillRepository) {
        this.taskRepository = taskRepository;
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AiTaskAssignmentResponse getTaskAssignmentSuggestion(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        Project project = task.getProject();
        List<ProjectMember> members = projectMemberRepository.findByProjectId(project.getId());

        if (members.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project has no members to assign");
        }

        String titleDesc = (task.getTitle() + " " + (task.getDescription() != null ? task.getDescription() : ""))
                .toLowerCase(Locale.ROOT);

        ProjectMember bestMember = null;
        double bestScore = -1.0;

        // Group tasks by assignee to check workloads
        List<Task> projectTasks = taskRepository.findByProjectId(project.getId());
        Map<String, Long> userWorkloads = new HashMap<>();
        for (ProjectMember m : members) {
            String userId = m.getUser().getId();
            long count = projectTasks.stream()
                    .filter(t -> t.getTaskAssignments().stream().anyMatch(a -> a.getUser().getId().equals(userId)))
                    .count();
            userWorkloads.put(userId, count);
        }

        for (ProjectMember m : members) {
            User user = m.getUser();
            List<UserSkill> skills = userSkillRepository.findByUserId(user.getId());

            double skillMatchScore = 0.0;
            boolean hasMatchedSkill = false;

            for (UserSkill us : skills) {
                String skillName = us.getSkill().getName().toLowerCase(Locale.ROOT);
                if (titleDesc.contains(skillName)) {
                    // Level is 1 to 5. Level 5 skill gives +1.0 match score.
                    skillMatchScore += us.getLevel() * 0.2;
                    hasMatchedSkill = true;
                }
            }

            // Workload balance factor: fewer tasks gives up to +0.2 boost
            long workload = userWorkloads.getOrDefault(user.getId(), 0L);
            double workloadFactor = Math.max(0.0, 0.2 - (workload * 0.04));

            double totalScore = skillMatchScore + workloadFactor;

            // If no skills matched but user is free, give a small default score
            if (!hasMatchedSkill) {
                totalScore = 0.3 + workloadFactor;
            }

            if (totalScore > bestScore) {
                bestScore = totalScore;
                bestMember = m;
            }
        }

        // Limit score to maximum 1.0, round to 2 decimal places
        double finalScore = Math.min(1.0, Math.round(bestScore * 100.0) / 100.0);
        if (bestMember == null) {
            bestMember = members.get(0);
            finalScore = 0.5;
        }

        return AiTaskAssignmentResponse.builder()
                .userId(bestMember.getUser().getId())
                .userName(bestMember.getUser().getFullName())
                .score(finalScore)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AiSprintPredictionResponse getSprintPrediction(String sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));

        if ("COMPLETED".equalsIgnoreCase(sprint.getStatus())) {
            return AiSprintPredictionResponse.builder()
                    .completionProbability(100)
                    .riskLevel("LOW")
                    .build();
        }

        List<Task> tasks = taskRepository.findBySprintId(sprintId);
        if (tasks.isEmpty()) {
            return AiSprintPredictionResponse.builder()
                    .completionProbability(100)
                    .riskLevel("LOW")
                    .build();
        }

        int totalPoints = tasks.stream()
                .mapToInt(t -> t.getStoryPoint() != null ? t.getStoryPoint() : 0)
                .sum();
        int completedPoints = tasks.stream()
                .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
                .mapToInt(t -> t.getStoryPoint() != null ? t.getStoryPoint() : 0)
                .sum();

        LocalDate startDate = sprint.getStartDate() != null ? sprint.getStartDate() : LocalDate.now().minusDays(5);
        LocalDate endDate = sprint.getEndDate() != null ? sprint.getEndDate() : LocalDate.now().plusDays(5);

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        long elapsedDays = ChronoUnit.DAYS.between(startDate, LocalDate.now());

        if (totalDays <= 0) totalDays = 1;
        if (elapsedDays < 0) elapsedDays = 0;
        if (elapsedDays > totalDays) elapsedDays = totalDays;

        double progressRatio = totalPoints == 0 ? 1.0 : (double) completedPoints / totalPoints;
        double elapsedRatio = (double) elapsedDays / totalDays;

        int probability;
        if (elapsedRatio == 0) {
            probability = 80; // Baseline
        } else {
            // Formula: progress relative to time spent + remaining sprint buffer
            double rawProb = (progressRatio / elapsedRatio) * 70.0 + (1.0 - elapsedRatio) * 30.0;
            probability = (int) Math.min(100, Math.max(10, rawProb));
        }

        String riskLevel = "MEDIUM";
        if (probability >= 80) {
            riskLevel = "LOW";
        } else if (probability < 45) {
            riskLevel = "HIGH";
        }

        return AiSprintPredictionResponse.builder()
                .completionProbability(probability)
                .riskLevel(riskLevel)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AiProjectSummaryResponse getProjectSummary(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> "DONE".equalsIgnoreCase(t.getStatus())).count();
        double completionRate = totalTasks == 0 ? 0.0 : (double) completedTasks / totalTasks * 100.0;

        List<Sprint> sprints = sprintRepository.findByProjectId(projectId);
        List<Sprint> activeSprints = sprints.stream()
                .filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus()))
                .collect(Collectors.toList());

        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Project '%s' (from %s to %s) has a total of %d tasks, with %d completed (%.1f%% complete). ",
                project.getName(), project.getStartDate(), project.getEndDate(), totalTasks, completedTasks, completionRate));

        if (!activeSprints.isEmpty()) {
            Sprint active = activeSprints.get(0);
            List<Task> sprintTasks = taskRepository.findBySprintId(active.getId());
            long totalSprintTasks = sprintTasks.size();
            long completedSprintTasks = sprintTasks.stream().filter(t -> "DONE".equalsIgnoreCase(t.getStatus())).count();
            double sprintCompletionRate = totalSprintTasks == 0 ? 0.0 : (double) completedSprintTasks / totalSprintTasks * 100.0;
            
            summary.append(String.format("Sprint '%s' is currently active and ends on %s. It contains %d tasks and is %.1f%% completed. ",
                    active.getName(), active.getEndDate(), totalSprintTasks, sprintCompletionRate));
        } else {
            summary.append("There are currently no active sprints in this project. ");
        }

        // Add overall health evaluation
        if (completionRate >= 75.0) {
            summary.append("Overall, the project is in excellent health and on track to deliver its goals.");
        } else if (completionRate >= 40.0) {
            summary.append("Overall, the project is making steady progress, though performance can be further optimized by addressing high priority backlog items.");
        } else {
            summary.append("Overall, the project progress is low. It is recommended to re-evaluate tasks and distribute the workload to avoid potential delivery delays.");
        }

        return AiProjectSummaryResponse.builder()
                .summary(summary.toString())
                .build();
    }
}
