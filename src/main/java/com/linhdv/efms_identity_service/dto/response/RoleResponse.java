package com.linhdv.efms_identity_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoleResponse {
    private UUID id;
    private String name;
    private String description;
    private Boolean isActive;
    private Instant createdAt;
    private List<PermissionResponse> permissions;
}
