package com.example.jira_clone.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.jira_clone.dto.user.UserDetailDto;
import com.example.jira_clone.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    @SecurityRequirement(name = "bearerAuth")
    public UserDetailDto getDetail(Authentication authentication) {
        return userService.getUserDetail(authentication.getName());
    }
    
}
