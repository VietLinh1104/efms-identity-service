package com.linhdv.efms_identity_service.controller;

import com.linhdv.efms_identity_service.entity.Company;
import com.linhdv.efms_identity_service.entity.Role;
import com.linhdv.efms_identity_service.entity.User;
import com.linhdv.efms_identity_service.payload.request.LoginRequest;
import com.linhdv.efms_identity_service.payload.request.RegisterRequest;
import com.linhdv.efms_identity_service.payload.response.JwtResponse;
import com.linhdv.efms_identity_service.payload.response.MessageResponse;
import com.linhdv.efms_identity_service.repository.CompanyRepository;
import com.linhdv.efms_identity_service.repository.RoleRepository;
import com.linhdv.efms_identity_service.repository.UserRepository;
import com.linhdv.efms_identity_service.security.jwt.JwtUtils;
import com.linhdv.efms_identity_service.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @org.springframework.beans.factory.annotation.Value("${resend.api-key}")
    private String resendApiKey;

    @org.springframework.beans.factory.annotation.Value("${resend.from-email}")
    private String fromEmail;

    private static class OtpData {
        String code;
        java.time.Instant expiry;

        OtpData(String code, java.time.Instant expiry) {
            this.code = code;
            this.expiry = expiry;
        }
    }

    private final java.util.Map<String, OtpData> otpStorage = new java.util.concurrent.ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getCompanyId(),
                roles));
    }

    @PostMapping("/register/send-code/{email}")
    public ResponseEntity<?> sendRegistrationCode(@PathVariable String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is required!"));
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        otpStorage.put(email, new OtpData(otp, java.time.Instant.now().plus(5, java.time.temporal.ChronoUnit.MINUTES)));

        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("from", fromEmail);
            requestBody.put("to", new String[]{email});
            requestBody.put("subject", "Mã xác thực đăng ký tài khoản - EFMS");
            requestBody.put("html", "<p>Xin chào,</p><p>Mã xác thực đăng ký tài khoản của bạn là: <strong>" + otp + "</strong></p><p>Mã này sẽ hết hạn trong 5 phút.</p><p>Trân trọng!</p>");

            org.springframework.http.HttpEntity<java.util.Map<String, Object>> request = new org.springframework.http.HttpEntity<>(requestBody, headers);
            
            restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
        } catch (Exception e) {
            logger.error("Failed to send OTP email via Resend", e);
            return ResponseEntity.internalServerError().body(new MessageResponse("Error: Failed to send email!"));
        }

        return ResponseEntity.ok(new MessageResponse("Verification code sent successfully!"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        OtpData otpData = otpStorage.get(signUpRequest.getEmail());
        if (otpData == null || otpData.expiry.isBefore(java.time.Instant.now())
                || !otpData.code.equals(signUpRequest.getOtp())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid or expired verification code!"));
        }

        otpStorage.remove(signUpRequest.getEmail());

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new company
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setName(signUpRequest.getCompanyName());
        company.setCurrency("VND");
        company.setIsActive(true);
        company = companyRepository.save(company);

        // Find or create Admin role
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setId(UUID.randomUUID());
                    role.setName("ROLE_ADMIN");
                    role.setIsActive(true);
                    return roleRepository.save(role);
                });

        // Create new user's account
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setCompany(company);
        user.setRole(adminRole);
        user.setIsActive(true);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
