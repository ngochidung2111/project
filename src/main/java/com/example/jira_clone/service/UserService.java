package com.example.jira_clone.service;

import com.example.jira_clone.dto.user.UserDetailDto;
import com.example.jira_clone.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);

    User findById(String id);

    UserDetailDto getUserDetail(String userId);
}
