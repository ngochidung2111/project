package com.example.jira_clone.service;

import com.example.jira_clone.dto.auth.AuthResponse;
import com.example.jira_clone.dto.auth.LoginRequest;
import com.example.jira_clone.dto.auth.SignupRequest;
import com.example.jira_clone.dto.auth.UpdateProfileRequest;
import com.example.jira_clone.dto.auth.ChangePasswordRequest;
import com.example.jira_clone.dto.auth.UserDto;
import com.example.jira_clone.entity.Role;
import com.example.jira_clone.entity.UserRole;
import com.example.jira_clone.entity.User;
import com.example.jira_clone.repository.RoleRepository;
import com.example.jira_clone.security.JwtService;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserService userService,
            RoleRepository roleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userService.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .avatarUrl(request.avatarUrl())
                .status("ACTIVE")
                .build();

        User savedUser = userService.save(user);
        Role defaultRole = roleRepository.findByCode("USER")
                .orElseGet(() -> roleRepository
                        .save(Role.builder().code("USER").description("Default project member").build()));
        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(defaultRole)
                .build();
        savedUser.getUserRoles().add(userRole);

        String token = jwtService.generateToken(savedUser);
        return toResponse(savedUser, token);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password()));

        User user = userService.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String token = jwtService.generateToken(user);
        return toResponse(user, token);
    }

    @Transactional(readOnly = true)
    public UserDto me(String email) {
        User user = userService.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toUserDto(user);
    }

    @Transactional
    public UserDto updateProfile(String email, UpdateProfileRequest request) {
        User user = userService.findByEmail(normalizeEmail(email))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setFullName(request.fullName());
        user.setAvatarUrl(request.avatarUrl());

        User savedUser = userService.save(user);
        return toUserDto(savedUser);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userService.findByEmail(normalizeEmail(email))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid old password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userService.save(user);
    }

    private AuthResponse toResponse(User user, String token) {
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds(), toUserDto(user));
    }

    private UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getStatus());
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}