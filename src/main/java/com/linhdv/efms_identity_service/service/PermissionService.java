package com.linhdv.efms_identity_service.service;

import com.linhdv.efms_identity_service.dto.request.PermissionRequest;
import com.linhdv.efms_identity_service.dto.response.PermissionResponse;
import com.linhdv.efms_identity_service.entity.Permission;
import com.linhdv.efms_identity_service.mapper.PermissionMapper;
import com.linhdv.efms_identity_service.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        return permissionMapper.toResponse(permission);
    }

    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toEntity(request);
        permission.setId(UUID.randomUUID());
        permission.setCreatedAt(Instant.now());
        permission = permissionRepository.save(permission);
        return permissionMapper.toResponse(permission);
    }

    @Transactional
    public PermissionResponse updatePermission(UUID id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        permissionMapper.updateEntity(request, permission);
        permission = permissionRepository.save(permission);
        return permissionMapper.toResponse(permission);
    }

    @Transactional
    public void deletePermission(UUID id) {
        permissionRepository.deleteById(id);
    }
}
