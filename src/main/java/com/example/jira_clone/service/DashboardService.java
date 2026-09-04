package com.example.jira_clone.service;

import com.example.jira_clone.dto.project.DashboardOverviewDto;
import com.example.jira_clone.dto.project.BurndownPointDto;
import com.example.jira_clone.dto.project.VelocityPointDto;
import com.example.jira_clone.dto.project.WorkloadDto;

import java.util.List;

public interface DashboardService {
    DashboardOverviewDto getDashboardOverview(String projectId);
    List<BurndownPointDto> getBurndownChart(String projectId);
    List<VelocityPointDto> getVelocityChart(String projectId);
    List<WorkloadDto> getTeamWorkload(String projectId);
}
