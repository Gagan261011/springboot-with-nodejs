package com.example.usermgmt.controller;

import com.example.usermgmt.dto.PageResponse;
import com.example.usermgmt.dto.UserCreateRequest;
import com.example.usermgmt.dto.UserPatchRequest;
import com.example.usermgmt.dto.UserResponse;
import com.example.usermgmt.dto.UserUpdateRequest;
import com.example.usermgmt.entity.UserRole;
import com.example.usermgmt.entity.UserStatus;
import com.example.usermgmt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "User Management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List users with pagination, sorting, and filtering")
    @GetMapping
    public PageResponse<UserResponse> listUsers(
            @Parameter(description = "Search term applied to first name, last name, or email")
            @RequestParam(required = false)
            String search,
            @Parameter(description = "Filter by role")
            @RequestParam(required = false)
            UserRole role,
            @Parameter(description = "Filter by status")
            @RequestParam(required = false)
            UserStatus status,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size,
            @Parameter(description = "Sort fields, e.g. sort=firstName,asc&sort=updatedAt,desc")
            @RequestParam(name = "sort", required = false)
            List<String> sort) {
        return userService.getUsers(search, role, status, page, size, sort);
    }

    @Operation(summary = "Get a user by identifier")
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @Operation(summary = "Update a user entirely")
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @Operation(summary = "Patch a user with provided fields only")
    @PatchMapping("/{id}")
    public UserResponse patchUser(@PathVariable UUID id, @Valid @RequestBody UserPatchRequest request) {
        return userService.patchUser(id, request);
    }

    @Operation(summary = "Soft delete a user by marking status as INACTIVE")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

