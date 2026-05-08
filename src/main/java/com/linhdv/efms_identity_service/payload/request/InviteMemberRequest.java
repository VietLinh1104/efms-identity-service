package com.linhdv.efms_identity_service.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InviteMemberRequest {
    @NotBlank
    @Size(max = 255)
    @Email
    private String email;

    @NotNull
    private UUID roleId;
}
