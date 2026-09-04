package com.example.jira_clone.dto.task;

import lombok.Builder;
import java.util.List;

@Builder
public record KanbanBoardDto(
    List<TaskDto> todo,
    List<TaskDto> inProgress,
    List<TaskDto> testing,
    List<TaskDto> done
) {}
