package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.user.AddUserSkillRequest;
import com.example.jira_clone.dto.user.UpdateUserSkillRequest;
import com.example.jira_clone.dto.user.UserSkillDto;
import com.example.jira_clone.entity.Skill;
import com.example.jira_clone.entity.User;
import com.example.jira_clone.entity.UserSkill;
import com.example.jira_clone.repository.SkillRepository;
import com.example.jira_clone.repository.UserRepository;
import com.example.jira_clone.repository.UserSkillRepository;
import com.example.jira_clone.service.UserSkillService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSkillServiceImpl implements UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public UserSkillServiceImpl(UserSkillRepository userSkillRepository, UserRepository userRepository, SkillRepository skillRepository) {
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSkillDto> getUserSkills(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
        }
        return userSkillRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserSkillDto addUserSkill(String userId, AddUserSkillRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found with id: " + request.skillId()));

        if (userSkillRepository.findByUserIdAndSkillId(userId, request.skillId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has this skill");
        }

        UserSkill userSkill = UserSkill.builder()
                .user(user)
                .skill(skill)
                .level(request.level())
                .build();

        UserSkill savedUserSkill = userSkillRepository.save(userSkill);
        return toDto(savedUserSkill);
    }

    @Override
    @Transactional
    public UserSkillDto updateUserSkill(String userId, Long skillId, UpdateUserSkillRequest request) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User skill association not found"));

        userSkill.setLevel(request.level());
        UserSkill updatedUserSkill = userSkillRepository.save(userSkill);
        return toDto(updatedUserSkill);
    }

    @Override
    @Transactional
    public void deleteUserSkill(String userId, Long skillId) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User skill association not found"));
        userSkillRepository.delete(userSkill);
    }

    private UserSkillDto toDto(UserSkill userSkill) {
        return new UserSkillDto(
                userSkill.getSkill().getId(),
                userSkill.getSkill().getName(),
                userSkill.getLevel()
        );
    }
}
