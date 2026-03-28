package com.linhdv.efms_identity_service.repository;

import com.linhdv.efms_identity_service.entity.RolePermission;
import com.linhdv.efms_identity_service.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    List<RolePermission> findByRoleId(UUID roleId);
    void deleteByRoleId(UUID roleId);
}
