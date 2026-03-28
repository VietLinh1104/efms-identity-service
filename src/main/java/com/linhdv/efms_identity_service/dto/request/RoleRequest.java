package com.linhdv.efms_identity_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoleRequest {
    @NotBlank
    @Size(max = 50)
    private String name;

    private String description;

    private Boolean isActive = true;

    private List<UUID> permissionIds;
}
