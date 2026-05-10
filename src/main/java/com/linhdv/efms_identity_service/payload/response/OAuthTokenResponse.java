package com.linhdv.efms_identity_service.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthTokenResponse {
    private String access_token;
    private String token_type = "Bearer";
    private Long expires_in;
    private String scope;
    private String refresh_token;

    public OAuthTokenResponse(String accessToken, Long expiresIn) {
        this.access_token = accessToken;
        this.expires_in = expiresIn;
    }
}
