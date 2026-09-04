package com.example.jira_clone.controller;

import com.example.jira_clone.dto.user.AddUserSkillRequest;
import com.example.jira_clone.dto.user.UpdateUserSkillRequest;
import com.example.jira_clone.dto.user.UserSkillDto;
import com.example.jira_clone.service.UserSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/skills")
@SecurityRequirement(name = "bearerAuth")
public class UserSkillController {

    private final UserSkillService userSkillService;

    public UserSkillController(UserSkillService userSkillService) {
        this.userSkillService = userSkillService;
    }

    @GetMapping
    @Operation(summary = "Get all skills for a user")
    @PreAuthorize("@userSecurity.isOwner(authentication, #userId) or hasRole('ADMIN')")
    public ResponseEntity<List<UserSkillDto>> getUserSkills(@PathVariable String userId) {
        return ResponseEntity.ok(userSkillService.getUserSkills(userId));
    }

    @PostMapping
    @Operation(summary = "Add a skill to a user")
    @PreAuthorize("@userSecurity.isOwner(authentication, #userId) or hasRole('ADMIN')")
    public ResponseEntity<UserSkillDto> addUserSkill(@PathVariable String userId, @Valid @RequestBody AddUserSkillRequest request) {
        UserSkillDto createdUserSkill = userSkillService.addUserSkill(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserSkill);
    }

    @PutMapping("/{skillId}")
    @Operation(summary = "Update a user's skill level")
    @PreAuthorize("@userSecurity.isOwner(authentication, #userId) or hasRole('ADMIN')")
    public ResponseEntity<UserSkillDto> updateUserSkill(@PathVariable String userId, @PathVariable Long skillId, @Valid @RequestBody UpdateUserSkillRequest request) {
        return ResponseEntity.ok(userSkillService.updateUserSkill(userId, skillId, request));
    }

    @DeleteMapping("/{skillId}")
    @Operation(summary = "Remove a skill from a user")
    @PreAuthorize("@userSecurity.isOwner(authentication, #userId) or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserSkill(@PathVariable String userId, @PathVariable Long skillId) {
        userSkillService.deleteUserSkill(userId, skillId);
    }
}
