package com.linhdv.efms_identity_service.mapper;

import com.linhdv.efms_identity_service.dto.response.AuditLogResponse;
import com.linhdv.efms_identity_service.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    AuditLogMapper INSTANCE = Mappers.getMapper(AuditLogMapper.class);

    @Mapping(target = "changedByName", source = "changedBy.name")
    AuditLogResponse toResponse(AuditLog auditLog);
}
