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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
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

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
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
