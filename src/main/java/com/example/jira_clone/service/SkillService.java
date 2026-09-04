package com.example.jira_clone.service;

import com.example.jira_clone.dto.skill.SkillDto;
import com.example.jira_clone.dto.skill.SkillRequest;

import java.util.List;

public interface SkillService {
    List<SkillDto> findAll();
    SkillDto findById(Long id);
    SkillDto createSkill(SkillRequest request);
    void deleteById(Long id);
    SkillDto updateSkill(Long id, SkillRequest request);
}