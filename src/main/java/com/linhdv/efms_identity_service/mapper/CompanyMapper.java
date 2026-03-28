package com.linhdv.efms_identity_service.mapper;

import com.linhdv.efms_identity_service.dto.request.CompanyRequest;
import com.linhdv.efms_identity_service.dto.response.CompanyResponse;
import com.linhdv.efms_identity_service.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    CompanyResponse toResponse(Company company);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Company toEntity(CompanyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(CompanyRequest request, @MappingTarget Company company);
}
