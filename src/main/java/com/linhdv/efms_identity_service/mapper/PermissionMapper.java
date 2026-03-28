package com.linhdv.efms_identity_service.mapper;

import com.linhdv.efms_identity_service.dto.request.PermissionRequest;
import com.linhdv.efms_identity_service.dto.response.PermissionResponse;
import com.linhdv.efms_identity_service.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    PermissionResponse toResponse(Permission permission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Permission toEntity(PermissionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(PermissionRequest request, @MappingTarget Permission permission);
}
