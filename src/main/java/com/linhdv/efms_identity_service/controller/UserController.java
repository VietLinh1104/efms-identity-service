package com.linhdv.efms_identity_service.controller;

import com.linhdv.efms_identity_service.dto.common.ApiResponse;
import com.linhdv.efms_identity_service.dto.request.UserUpdateRequest;
import com.linhdv.efms_identity_service.dto.response.UserResponse;
import com.linhdv.efms_identity_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable UUID id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success("User updated successfully", userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ApiResponse.success("User deleted successfully");
    }
}
