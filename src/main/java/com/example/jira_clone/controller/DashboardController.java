package com.example.jira_clone.controller;

import com.example.jira_clone.dto.project.DashboardOverviewDto;
import com.example.jira_clone.dto.project.BurndownPointDto;
import com.example.jira_clone.dto.project.VelocityPointDto;
import com.example.jira_clone.dto.project.WorkloadDto;
import com.example.jira_clone.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{projectId}/dashboard")
    @Operation(summary = "Get project dashboard overview statistics")
    public ResponseEntity<DashboardOverviewDto> getDashboardOverview(@PathVariable String projectId) {
        return ResponseEntity.ok(dashboardService.getDashboardOverview(projectId));
    }

    @GetMapping("/{projectId}/burndown")
    @Operation(summary = "Get project burndown chart data")
    public ResponseEntity<List<BurndownPointDto>> getBurndownChart(@PathVariable String projectId) {
        return ResponseEntity.ok(dashboardService.getBurndownChart(projectId));
    }

    @GetMapping("/{projectId}/velocity")
    @Operation(summary = "Get project velocity chart data")
    public ResponseEntity<List<VelocityPointDto>> getVelocityChart(@PathVariable String projectId) {
        return ResponseEntity.ok(dashboardService.getVelocityChart(projectId));
    }

    @GetMapping("/{projectId}/workload")
    @Operation(summary = "Get project members workload data")
    public ResponseEntity<List<WorkloadDto>> getTeamWorkload(@PathVariable String projectId) {
        return ResponseEntity.ok(dashboardService.getTeamWorkload(projectId));
    }
}
