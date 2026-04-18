package com.linhdv.efms_identity_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInternalResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String avatar;
}
