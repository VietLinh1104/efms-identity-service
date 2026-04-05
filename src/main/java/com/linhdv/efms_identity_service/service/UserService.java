package com.linhdv.efms_identity_service.service;

import com.linhdv.efms_identity_service.dto.request.UserUpdateRequest;
import com.linhdv.efms_identity_service.dto.response.UserResponse;
import com.linhdv.efms_identity_service.entity.*;
import com.linhdv.efms_identity_service.mapper.UserMapper;
import com.linhdv.efms_identity_service.repository.RoleRepository;
import com.linhdv.efms_identity_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (request.getName() != null)
            user.setName(request.getName());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getIsActive() != null)
            user.setIsActive(request.getIsActive());

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + request.getRoleId()));
            user.setRole(role);
        }

        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
