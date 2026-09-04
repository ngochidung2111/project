package com.example.jira_clone.service;

import com.example.jira_clone.dto.user.CreateUserRequest;
import com.example.jira_clone.dto.user.UpdateUserRequest;
import com.example.jira_clone.dto.user.UserDetailDto;
import com.example.jira_clone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);

    User findById(String id);

    UserDetailDto getUserDetail(String userId);

    Page<UserDetailDto> findAll(String keyword, Pageable pageable);

    UserDetailDto createUser(CreateUserRequest request);

    UserDetailDto updateUser(String id, UpdateUserRequest request);

    void deleteUser(String id);
}
