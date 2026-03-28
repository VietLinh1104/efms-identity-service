package com.linhdv.efms_identity_service.controller;

import com.linhdv.efms_identity_service.dto.common.ApiResponse;
import com.linhdv.efms_identity_service.dto.request.PermissionRequest;
import com.linhdv.efms_identity_service.dto.response.PermissionResponse;
import com.linhdv.efms_identity_service.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.success(permissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionResponse> getPermissionById(@PathVariable UUID id) {
        return ApiResponse.success(permissionService.getPermissionById(id));
    }

    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {
        return ApiResponse.success("Permission created successfully", permissionService.createPermission(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> updatePermission(@PathVariable UUID id, @Valid @RequestBody PermissionRequest request) {
        return ApiResponse.success("Permission updated successfully", permissionService.updatePermission(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ApiResponse.success("Permission deleted successfully");
    }
}
