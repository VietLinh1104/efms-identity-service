package com.linhdv.efms_identity_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Boolean isActive;
    private Instant createdAt;
    private CompanyResponse company;
    private RoleResponse role;
}
