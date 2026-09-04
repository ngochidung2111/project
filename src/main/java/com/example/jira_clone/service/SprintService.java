package com.example.jira_clone.service;

import com.example.jira_clone.dto.sprint.SprintDto;
import com.example.jira_clone.dto.sprint.SprintMetricsDto;
import com.example.jira_clone.dto.sprint.SprintRequest;

import java.util.List;

public interface SprintService {
    List<SprintDto> getSprintsByProject(String projectId);
    SprintDto getSprintById(String id);
    SprintDto createSprint(String projectId, SprintRequest request);
    SprintDto updateSprint(String id, SprintRequest request);
    void deleteSprint(String id);
    SprintDto startSprint(String id);
    SprintDto completeSprint(String id);
    SprintMetricsDto getSprintMetrics(String id);
}
