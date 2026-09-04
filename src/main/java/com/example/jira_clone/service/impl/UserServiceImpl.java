package com.example.jira_clone.service.impl;

import com.example.jira_clone.dto.role.RoleDto;
import com.example.jira_clone.dto.user.CreateUserRequest;
import com.example.jira_clone.dto.user.UpdateUserRequest;
import com.example.jira_clone.dto.user.UserDetailDto;
import com.example.jira_clone.entity.Role;
import com.example.jira_clone.entity.User;
import com.example.jira_clone.entity.UserRole;
import com.example.jira_clone.repository.RoleRepository;
import com.example.jira_clone.repository.UserRepository;
import com.example.jira_clone.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email)).isPresent();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailDto getUserDetail(String identifier) {
        User user = userRepository.findByEmail(normalizeEmail(identifier))
                .or(() -> userRepository.findById(identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with identifier: " + identifier));
        return toUserDetailDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailDto> findAll(String keyword, Pageable pageable) {
        Page<User> userPage;
        if (keyword != null && !keyword.isBlank()) {
            userPage = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(this::toUserDetailDto);
    }

    @Override
    @Transactional
    public UserDetailDto createUser(CreateUserRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .avatarUrl(request.avatarUrl())
                .status("ACTIVE")
                .build();

        Role defaultRole = roleRepository.findByCode("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().code("USER").description("Default project member").build()));
        UserRole userRole = UserRole.builder().user(user).role(defaultRole).build();
        user.getUserRoles().add(userRole);

        User savedUser = userRepository.save(user);
        return toUserDetailDto(savedUser);
    }

    @Override
    @Transactional
    public UserDetailDto updateUser(String id, UpdateUserRequest request) {
        User user = findById(id);
        user.setFullName(request.fullName());
        user.setAvatarUrl(request.avatarUrl());
        user.setStatus(request.status());

        User updatedUser = userRepository.save(user);
        return toUserDetailDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDetailDto toUserDetailDto(User user) {
        RoleDto roleDto = user.getUserRoles().stream()
                .findFirst()
                .map(ur -> new RoleDto(ur.getRole().getId(), ur.getRole().getCode(), ur.getRole().getDescription()))
                .orElse(null);

        return new UserDetailDto(user.getId(), user.getEmail(), user.getFullName(), roleDto);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}