package com.linhdv.efms_identity_service.security.services;

import com.linhdv.efms_identity_service.entity.User;
import com.linhdv.efms_identity_service.repository.RolePermissionRepository;
import com.linhdv.efms_identity_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        List<String> permissions = rolePermissionRepository.findByRoleId(user.getRole().getId())
                .stream()
                .map(rp -> rp.getPermission().getResource() + ":" + rp.getPermission().getAction())
                .collect(Collectors.toList());

        return UserDetailsImpl.build(user, permissions);
    }
}
