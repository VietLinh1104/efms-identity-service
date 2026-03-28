package com.linhdv.efms_identity_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PermissionResponse {
    private UUID id;
    private String resource;
    private String action;
    private String description;
    private Instant createdAt;
}
