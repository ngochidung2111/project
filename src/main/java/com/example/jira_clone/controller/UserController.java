package com.example.jira_clone.controller;

import com.example.jira_clone.dto.user.CreateUserRequest;
import com.example.jira_clone.dto.user.UpdateUserRequest;
import com.example.jira_clone.dto.user.UserDetailDto;
import com.example.jira_clone.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get a paginated list of users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDetailDto>> getUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(keyword, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user details by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserDetail(id));
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDetailDto createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailDto> updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name or email")
    public ResponseEntity<java.util.List<UserDetailDto>> searchUsers(
            @RequestParam(required = false) String keyword) {
        org.springframework.data.domain.Page<UserDetailDto> users = userService.findAll(
                keyword, 
                org.springframework.data.domain.PageRequest.of(0, 50)
        );
        return ResponseEntity.ok(users.getContent());
    }
}
