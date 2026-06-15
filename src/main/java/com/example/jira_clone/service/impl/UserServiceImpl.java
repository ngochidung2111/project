package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.role.RoleDto;
import com.example.jira_clone.dto.user.UserDetailDto;
import com.example.jira_clone.entity.User;
import com.example.jira_clone.repository.UserRepository;
import com.example.jira_clone.service.UserService;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public UserDetailDto getUserDetail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        RoleDto roleDto = user.getUserRoles().stream()
                .findFirst()
                .map(ur -> new RoleDto(ur.getRole().getId(), ur.getRole().getCode(), ur.getRole().getDescription()))
                .orElse(null);

        return new UserDetailDto(user.getId(), user.getEmail(), user.getFullName(), roleDto);
    }
}