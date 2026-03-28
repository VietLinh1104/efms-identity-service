package com.linhdv.efms_identity_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserUpdateRequest {
    @Size(min = 3, max = 255)
    private String name;

    @Email
    @Size(max = 255)
    private String email;

    private Boolean isActive;

    private UUID roleId;
}
