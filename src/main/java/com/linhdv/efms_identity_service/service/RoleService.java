package com.linhdv.efms_identity_service.service;

import com.linhdv.efms_identity_service.dto.request.RoleRequest;
import com.linhdv.efms_identity_service.dto.response.RoleResponse;
import com.linhdv.efms_identity_service.entity.*;
import com.linhdv.efms_identity_service.mapper.RoleMapper;
import com.linhdv.efms_identity_service.repository.PermissionRepository;
import com.linhdv.efms_identity_service.repository.RolePermissionRepository;
import com.linhdv.efms_identity_service.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::enrichRoleWithPermissions)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return enrichRoleWithPermissions(role);
    }

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        Role role = roleMapper.toEntity(request);
        role.setId(UUID.randomUUID());
        role.setCreatedAt(Instant.now());
        role = roleRepository.save(role);

        if (request.getPermissionIds() != null) {
            for (UUID pId : request.getPermissionIds()) {
                assignPermissionToRole(role, pId);
            }
        }

        return enrichRoleWithPermissions(role);
    }

    @Transactional
    public RoleResponse updateRole(UUID id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        roleMapper.updateEntity(request, role);
        role = roleRepository.save(role);

        if (request.getPermissionIds() != null) {
            rolePermissionRepository.deleteByRoleId(id);
            for (UUID pId : request.getPermissionIds()) {
                assignPermissionToRole(role, pId);
            }
        }

        return enrichRoleWithPermissions(role);
    }

    @Transactional
    public void deleteRole(UUID id) {
        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.deleteById(id);
    }

    private void assignPermissionToRole(Role role, UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));
        
        RolePermission rolePermission = new RolePermission();
        RolePermissionId rpId = new RolePermissionId();
        rpId.setRoleId(role.getId());
        rpId.setPermissionId(permissionId);
        
        rolePermission.setId(rpId);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setCreatedAt(Instant.now());
        
        rolePermissionRepository.save(rolePermission);
    }

    private RoleResponse enrichRoleWithPermissions(Role role) {
        RoleResponse response = roleMapper.toResponse(role);
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(role.getId());
        // permissions are already partially mapped by RoleMapper if mapped correctly, but let's be explicit if needed
        return response;
    }
}
