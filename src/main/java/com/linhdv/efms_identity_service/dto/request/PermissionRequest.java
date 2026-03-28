package com.linhdv.efms_identity_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequest {
    @NotBlank
    @Size(max = 100)
    private String resource;

    @NotBlank
    @Size(max = 50)
    private String action;

    private String description;
}
