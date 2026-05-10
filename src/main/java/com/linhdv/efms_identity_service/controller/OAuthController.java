package com.linhdv.efms_identity_service.controller;

import com.linhdv.efms_identity_service.entity.OAuthClient;
import com.linhdv.efms_identity_service.payload.response.MessageResponse;
import com.linhdv.efms_identity_service.payload.response.OAuthTokenResponse;
import com.linhdv.efms_identity_service.repository.OAuthClientRepository;
import com.linhdv.efms_identity_service.security.jwt.JwtUtils;
import com.linhdv.efms_identity_service.security.services.UserDetailsImpl;
import com.linhdv.efms_identity_service.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private OAuthClientRepository oauthClientRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${efms.app.frontend-url:http://localhost:5173/login}")
    private String frontendLoginUrl;

    @Value("${efms.app.base-url:http://localhost:8080}")
    private String baseUrl;

    // Map code -> email
    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();

    @GetMapping("/.well-known/oauth-authorization-server")
    public ResponseEntity<?> getMetadata() {
        Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("issuer", baseUrl);
        metadata.put("authorization_endpoint", baseUrl + "/api/identity/oauth/authorize");
        metadata.put("token_endpoint", baseUrl + "/api/identity/oauth/token");
        metadata.put("response_types_supported", java.util.List.of("code"));
        metadata.put("grant_types_supported", java.util.List.of("authorization_code", "refresh_token"));
        metadata.put("code_challenge_methods_supported", java.util.List.of("S256"));
        
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("response_type") String responseType,
            @RequestParam(value = "state", required = false) String state) {

        Optional<OAuthClient> client = oauthClientRepository.findByClientId(clientId);
        if (client.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Client not found"));
        }

        // Kiểm tra redirect_uri có khớp với đăng ký không (hoặc chứa trong danh sách cho phép)
        if (!client.get().getRedirectUri().contains(redirectUri)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid redirect URI"));
        }

        // Chuyển hướng người dùng sang trang Login của Frontend
        // Gửi kèm các params để Frontend biết sau khi login xong cần quay lại đâu
        String loginUrl = UriComponentsBuilder.fromHttpUrl(frontendLoginUrl)
                .queryParam("oauth_client_id", clientId)
                .queryParam("oauth_redirect_uri", redirectUri)
                .queryParam("oauth_state", state)
                .build().toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", loginUrl)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(
            @RequestParam("token") String token,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam(value = "state", required = false) String state) {

        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Error: Invalid token"));
        }

        String email = jwtUtils.getUserNameFromJwtToken(token);
        String code = UUID.randomUUID().toString().replace("-", "");
        codeStorage.put(code, email);

        // Chuyển hướng quay lại ứng dụng khách (ví dụ Claude) kèm theo code
        String targetUrl = UriComponentsBuilder.fromHttpUrl(redirectUri)
                .queryParam("code", code)
                .queryParam("state", state)
                .build().toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", targetUrl)
                .build();
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(
            @RequestParam("grant_type") String grantType,
            @RequestParam("code") String code,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("redirect_uri") String redirectUri) {

        if (!"authorization_code".equals(grantType)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Unsupported grant type"));
        }

        Optional<OAuthClient> client = oauthClientRepository.findByClientId(clientId);
        if (client.isEmpty() || !client.get().getClientSecret().equals(clientSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Error: Invalid client credentials"));
        }

        String email = codeStorage.remove(code);
        if (email == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid or expired code"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
        String jwt = jwtUtils.generateTokenFromUserDetails(userDetails);

        return ResponseEntity.ok(new OAuthTokenResponse(jwt, 86400L)); // 24h
    }
}
