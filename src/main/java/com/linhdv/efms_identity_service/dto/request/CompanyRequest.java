package com.linhdv.efms_identity_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequest {
    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 10)
    private String currency = "VND";

    private String taxCode;

    private String address;

    private Boolean isActive = true;
}
