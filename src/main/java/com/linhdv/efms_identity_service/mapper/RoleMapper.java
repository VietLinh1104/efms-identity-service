package com.linhdv.efms_identity_service.mapper;

import com.linhdv.efms_identity_service.dto.request.RoleRequest;
import com.linhdv.efms_identity_service.dto.response.PermissionResponse;
import com.linhdv.efms_identity_service.dto.response.RoleResponse;
import com.linhdv.efms_identity_service.entity.Role;
import com.linhdv.efms_identity_service.entity.RolePermission;
import com.linhdv.efms_identity_service.repository.RolePermissionRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { PermissionMapper.class })
public abstract class RoleMapper {

    @Autowired
    protected RolePermissionRepository rolePermissionRepository;

    @Autowired
    protected PermissionMapper permissionMapper;

    public abstract RoleResponse toResponse(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Role toEntity(RoleRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract void updateEntity(RoleRequest request, @MappingTarget Role role);

    @AfterMapping
    protected void enrichPermissions(Role role, @MappingTarget RoleResponse response) {
        if (role != null && role.getId() != null) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(role.getId());
            List<PermissionResponse> permissions = rolePermissions.stream()
                    .map(rp -> permissionMapper.toResponse(rp.getPermission()))
                    .collect(Collectors.toList());
            response.setPermissions(permissions);
        }
    }
}
