package com.example.jira_clone.service;

import com.example.jira_clone.dto.user.AddUserSkillRequest;
import com.example.jira_clone.dto.user.UpdateUserSkillRequest;
import com.example.jira_clone.dto.user.UserSkillDto;

import java.util.List;

public interface UserSkillService {
    List<UserSkillDto> getUserSkills(String userId);
    UserSkillDto addUserSkill(String userId, AddUserSkillRequest request);
    UserSkillDto updateUserSkill(String userId, Long skillId, UpdateUserSkillRequest request);
    void deleteUserSkill(String userId, Long skillId);
}
