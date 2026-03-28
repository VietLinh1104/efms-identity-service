package com.linhdv.efms_identity_service.mapper;

import com.linhdv.efms_identity_service.dto.request.UserUpdateRequest;
import com.linhdv.efms_identity_service.dto.response.UserResponse;
import com.linhdv.efms_identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class, RoleMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", ignore = true) 
    void updateEntity(UserUpdateRequest request, @MappingTarget User user);
}
