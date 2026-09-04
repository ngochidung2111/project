package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.sprint.SprintDto;
import com.example.jira_clone.dto.sprint.SprintMetricsDto;
import com.example.jira_clone.dto.sprint.SprintRequest;
import com.example.jira_clone.entity.Project;
import com.example.jira_clone.entity.Sprint;
import com.example.jira_clone.entity.Task;
import com.example.jira_clone.repository.ProjectRepository;
import com.example.jira_clone.repository.SprintRepository;
import com.example.jira_clone.service.SprintService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintServiceImpl implements SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;

    public SprintServiceImpl(SprintRepository sprintRepository, ProjectRepository projectRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintDto> getSprintsByProject(String projectId) {
        return sprintRepository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SprintDto getSprintById(String id) {
        return sprintRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));
    }

    @Override
    @Transactional
    public SprintDto createSprint(String projectId, SprintRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        Sprint sprint = Sprint.builder()
                .project(project)
                .name(request.name())
                .goal(request.goal())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status("PLANNING")
                .build();

        return toDto(sprintRepository.save(sprint));
    }

    @Override
    @Transactional
    public SprintDto updateSprint(String id, SprintRequest request) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));

        sprint.setName(request.name());
        sprint.setGoal(request.goal());
        sprint.setStartDate(request.startDate());
        sprint.setEndDate(request.endDate());

        return toDto(sprintRepository.save(sprint));
    }

    @Override
    @Transactional
    public void deleteSprint(String id) {
        if (!sprintRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found");
        }
        sprintRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SprintDto startSprint(String id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));
        
        // Optionally complete other active sprints in the same project
        List<Sprint> activeSprints = sprintRepository.findByProjectIdAndStatus(sprint.getProject().getId(), "ACTIVE");
        for (Sprint s : activeSprints) {
            s.setStatus("COMPLETED");
            sprintRepository.save(s);
        }

        sprint.setStatus("ACTIVE");
        return toDto(sprintRepository.save(sprint));
    }

    @Override
    @Transactional
    public SprintDto completeSprint(String id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));

        sprint.setStatus("COMPLETED");
        return toDto(sprintRepository.save(sprint));
    }

    @Override
    @Transactional(readOnly = true)
    public SprintMetricsDto getSprintMetrics(String id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found"));

        List<Task> tasks = sprint.getTasks();
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(t -> "DONE".equalsIgnoreCase(t.getStatus())).count();
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        
        int totalStoryPoints = tasks.stream().mapToInt(t -> t.getStoryPoint() != null ? t.getStoryPoint() : 0).sum();
        int completedStoryPoints = tasks.stream()
                .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
                .mapToInt(t -> t.getStoryPoint() != null ? t.getStoryPoint() : 0)
                .sum();

        return SprintMetricsDto.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .completionRate(completionRate)
                .totalStoryPoints(totalStoryPoints)
                .completedStoryPoints(completedStoryPoints)
                .build();
    }

    private SprintDto toDto(Sprint sprint) {
        return SprintDto.builder()
                .id(sprint.getId())
                .projectId(sprint.getProject().getId())
                .name(sprint.getName())
                .goal(sprint.getGoal())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .status(sprint.getStatus())
                .build();
    }
}
