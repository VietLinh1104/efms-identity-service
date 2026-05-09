package com.linhdv.efms_identity_service.controller;

import com.linhdv.efms_identity_service.dto.common.ApiResponse;
import com.linhdv.efms_identity_service.dto.request.UserUpdateRequest;
import com.linhdv.efms_identity_service.dto.response.UserResponse;
import com.linhdv.efms_identity_service.service.UserService;
import jakarta.validation.Valid;
import com.linhdv.efms_identity_service.wrapper.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(userService.getAllUsers(page, size));
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
