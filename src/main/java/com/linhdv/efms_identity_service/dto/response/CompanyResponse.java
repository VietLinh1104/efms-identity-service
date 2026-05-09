package com.linhdv.efms_identity_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class CompanyResponse {
    private UUID id;
    private String name;
    private String currency;
    private String taxCode;
    private String address;
    private Boolean isActive;
    private Instant createdAt;
}
