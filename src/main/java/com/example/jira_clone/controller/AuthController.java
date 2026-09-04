package com.example.jira_clone.controller;

import com.example.jira_clone.dto.auth.AuthResponse;
import com.example.jira_clone.dto.auth.LoginRequest;
import com.example.jira_clone.dto.auth.SignupRequest;
import com.example.jira_clone.dto.auth.UserDto;
import com.example.jira_clone.dto.auth.UpdateProfileRequest;
import com.example.jira_clone.dto.auth.ChangePasswordRequest;
import com.example.jira_clone.service.AuthService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Create a new local user account")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(201).body(authService.signup(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT access token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication.getName()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update the current authenticated user's profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDto> updateProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(authentication.getName(), request));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change the current authenticated user's password")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh the JWT access token (Not Implemented)")
    public ResponseEntity<Void> refreshToken() {
        // TODO: Implement refresh token logic
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the current user (Not Implemented)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> logout() {
        // TODO: Implement logout logic (e.g., token blocklisting)
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}