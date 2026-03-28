package com.linhdv.efms_identity_service.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String name;
    private String email;
    private UUID companyId;
    private List<String> roles;

    public JwtResponse(String accessToken, UUID id, String name, String email, UUID companyId, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.name = name;
        this.email = email;
        this.companyId = companyId;
        this.roles = roles;
    }
}
