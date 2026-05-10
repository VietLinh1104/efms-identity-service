package com.linhdv.efms_identity_service.config;

import com.linhdv.efms_identity_service.entity.Permission;
import com.linhdv.efms_identity_service.entity.Role;
import com.linhdv.efms_identity_service.entity.RolePermission;
import com.linhdv.efms_identity_service.entity.RolePermissionId;
import com.linhdv.efms_identity_service.repository.PermissionRepository;
import com.linhdv.efms_identity_service.repository.RolePermissionRepository;
import com.linhdv.efms_identity_service.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public DatabaseSeeder(RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu database chưa có role nào thì tiến hành seed dữ liệu
        if (roleRepository.count() == 0 && permissionRepository.count() == 0) {
            seedRolesAndPermissions();
            System.out.println("✅ [DatabaseSeeder] Khởi tạo thành công Role và Permission mặc định!");
        }
    }

    private void seedRolesAndPermissions() {
        // 1. Khởi tạo Roles
        List<Role> roles = Arrays.asList(
                createRole("ROLE_ADMIN", "Quản trị viên toàn hệ thống"),
                createRole("ROLE_FINANCE_MANAGER", "Quản lý tài chính / Kế toán trưởng"),
                createRole("ROLE_ACCOUNTANT", "Kế toán viên"),
                createRole("ROLE_AUDITOR", "Kiểm toán viên"));
        roleRepository.saveAll(roles);

        // 2. Khởi tạo Permissions
        List<Permission> permissions = Arrays.asList(
                createPermission("user", "create", "Tạo người dùng mới"),
                createPermission("user", "read", "Xem thông tin người dùng"),
                createPermission("user", "update", "Cập nhật thông tin người dùng"),
                createPermission("user", "delete", "Xóa hoặc vô hiệu hóa người dùng"),

                createPermission("invoice", "create", "Tạo hóa đơn mới"),
                createPermission("invoice", "read", "Xem thông tin hóa đơn"),
                createPermission("invoice", "update", "Cập nhật hóa đơn"),
                createPermission("invoice", "delete", "Xóa hoặc hủy hóa đơn"),

                createPermission("payment", "create", "Tạo phiếu thanh toán"),
                createPermission("payment", "read", "Xem danh sách thanh toán"),
                createPermission("payment", "update", "Cập nhật thanh toán"),
                createPermission("payment", "delete", "Xóa phiếu thanh toán"),

                createPermission("report", "read", "Xem các báo cáo tài chính"));
        permissionRepository.saveAll(permissions);

        // Map tên Role để dễ dàng truy xuất
        Map<String, Role> roleMap = new HashMap<>();
        for (Role r : roles) {
            roleMap.put(r.getName(), r);
        }

        // 3. Khởi tạo Role_Permissions (Mapping)
        List<RolePermission> rolePermissions = new ArrayList<>();

        for (Permission p : permissions) {
            // ROLE_ADMIN: Tất cả quyền
            rolePermissions.add(createRolePermission(roleMap.get("ROLE_ADMIN"), p));

            // ROLE_FINANCE_MANAGER: Tất cả quyền trừ xóa user
            if (!(p.getResource().equals("user") && p.getAction().equals("delete"))) {
                rolePermissions.add(createRolePermission(roleMap.get("ROLE_FINANCE_MANAGER"), p));
            }

            // ROLE_ACCOUNTANT: Quyền trên Invoice và Payment, Xem user
            if (p.getResource().equals("invoice") || p.getResource().equals("payment") ||
                    (p.getResource().equals("user") && p.getAction().equals("read"))) {
                rolePermissions.add(createRolePermission(roleMap.get("ROLE_ACCOUNTANT"), p));
            }

            // ROLE_AUDITOR: Chỉ có quyền read trên tất cả module
            if (p.getAction().equals("read")) {
                rolePermissions.add(createRolePermission(roleMap.get("ROLE_AUDITOR"), p));
            }
        }

        rolePermissionRepository.saveAll(rolePermissions);
    }

    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(name);
        role.setDescription(description);
        role.setIsActive(true);
        return role;
    }

    private Permission createPermission(String resource, String action, String description) {
        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setResource(resource);
        permission.setAction(action);
        permission.setDescription(description);
        return permission;
    }

    private RolePermission createRolePermission(Role role, Permission permission) {
        RolePermission rp = new RolePermission();
        RolePermissionId rpId = new RolePermissionId();
        rpId.setRoleId(role.getId());
        rpId.setPermissionId(permission.getId());

        rp.setId(rpId);
        rp.setRole(role);
        rp.setPermission(permission);
        return rp;
    }
}
