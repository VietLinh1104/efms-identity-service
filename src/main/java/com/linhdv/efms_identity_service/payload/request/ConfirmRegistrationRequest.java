package com.linhdv.efms_identity_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmRegistrationRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;
}
