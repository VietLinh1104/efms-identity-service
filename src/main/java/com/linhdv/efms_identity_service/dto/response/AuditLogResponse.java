package com.linhdv.efms_identity_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class AuditLogResponse {
    private UUID id;
    private String tableName;
    private UUID recordId;
    private String action;
    private String changedByName;
    private Instant changedAt;
    private Map<String, Object> oldData;
    private Map<String, Object> newData;
}
