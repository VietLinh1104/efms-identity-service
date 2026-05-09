package com.linhdv.efms_identity_service.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linhdv.efms_identity_service.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String username;
    private String email;
    private UUID companyId;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;
    private List<String> permissions;

    public UserDetailsImpl(UUID id, String username, String email, UUID companyId, String password,
                           Collection<? extends GrantedAuthority> authorities, List<String> permissions) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.companyId = companyId;
        this.password = password;
        this.authorities = authorities;
        this.permissions = permissions;
    }

    public static UserDetailsImpl build(User user, List<String> permissions) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));

        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCompany().getId(),
                user.getPassword(),
                authorities,
                permissions);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
