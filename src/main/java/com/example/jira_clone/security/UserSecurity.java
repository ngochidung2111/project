package com.example.jira_clone.security;

import com.example.jira_clone.entity.User;
import com.example.jira_clone.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserService userService;

    public UserSecurity(UserService userService) {
        this.userService = userService;
    }

    public boolean isOwner(Authentication authentication, String userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String userEmail = authentication.getName();
        User user = userService.findByEmail(userEmail).orElse(null);
        return user != null && user.getId().equals(userId);
    }
}
